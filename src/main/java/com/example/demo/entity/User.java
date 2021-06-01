package com.example.demo.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.*;
import java.util.stream.Collectors;

@Table(name = "users")
@Entity
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Size(min = 5, max = 20, message = "Username must be at least 5 characters and at most 20 characters")
    @Column(unique = true, nullable = false)
    private String username;

    @Column(unique = true, nullable = false)
    private String email;

    @Size(min = 8, message = "Password must be at least 8 characters")
    // do not expose password in API response
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    @Column(name = "is_enabled", nullable = false, columnDefinition = "TINYINT(1) default 1")
    private final Integer isEnabled = 1;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "users_roles",
            joinColumns = @JoinColumn(
                    name = "user_id", referencedColumnName = "id"
            ),
            inverseJoinColumns = @JoinColumn(
                    name = "role_id", referencedColumnName = "id"
            )
    )

    // To hide roles in API response
    // use @JsonIgnore on class member and getter, use JsonProperty on setter method
    @JsonIgnore
    private List<Role> roles;
    @JsonIgnore
    public List<Role> getRoles() {
        return roles;
    }
    @JsonProperty
    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<>();
        List<Role> roles = this.getRoles();
        for (Role role : roles) {
            authorities.add(new SimpleGrantedAuthority(role.getName()));
        }
        return authorities;
    }

    @Override
    public boolean isAccountNonExpired() {
        return false;
    }

    @Override
    public boolean isAccountNonLocked() {
        return false;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    // 用 transient 讓這個 permission 不會建立在 table 的欄位
    @Transient
    private String[] permissionNames;
    public String[] getPermissionsNames() {
        String[] permissions = getRoles().stream().filter(Objects::nonNull)
                .map(Role::getPermissions).flatMap(Collection::stream)
                .map(permission-> permission.getName())
                .distinct()
                .toArray(String[]::new);
        return permissions;
    }

    @Transient
    private List<Permission> permissions;
    public List<Permission> getPermissions() {
        List<Permission> permissions = getRoles().stream().filter(Objects::nonNull)
            .map(Role::getPermissions).flatMap(Collection::stream)
            .distinct()
            .collect(Collectors.toList());
        return permissions;
    }



    @Override
    public String toString() {
        return String.format("User[" +
                        "id=%d, " +
                        "username='%s', " +
                        "email='%s', " +
                        "roles='%s', " +
                        "is_enabled='%s']",
                id,
                username,
                email,
                getRoles(),
                isEnabled
        );
    }
}
