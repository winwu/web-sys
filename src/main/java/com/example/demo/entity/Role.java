package com.example.demo.entity;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "roles")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // e.g. ROLE_SYSTEM_ADMIN, ROLE_ADMIN, ROLE_ADMIN_MARKETING, etc.
    private String name;

    private String description;

    public Integer getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String desc) {
        this.description = desc;
    }

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "roles_permissions",
            joinColumns = @JoinColumn(
                    name = "role_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(
                    name = "permission_id", referencedColumnName = "id"))
    private List<Permission> permissions;

    public List<Permission> getPermissions() {
        return permissions;
    }
    public void setPermissions(List<Permission> permissions) {
        this.permissions = permissions;
    }

    @Override
    public String toString() {
        return String.format("Role[" +
                        "id=%d, " +
                        "name='%s', " +
                        "description='%s', + " +
                        "permissions='%s']",
                id,
                name,
                description,
                permissions
        );
    }
}