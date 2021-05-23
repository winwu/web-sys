package com.example.demo.entity;

import javax.persistence.*;

@Entity
@Table(name = "roles")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // e.g. ROLE_ADMIN, ROLE_CLIENT
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

//    @ManyToMany(fetch = FetchType.EAGER)
//    @JoinTable(name = "permissions", joinColumns = {
//            @JoinColumn(name = "role_id")
//        },
//            inverseJoinColumns = {@JoinColumn(name = "permission_id")}
//    )
//    private List<Permission> permission;

    @Override
    public String toString() {
        return String.format("Role[" +
                        "id=%d, " +
                        "name='%s', " +
                        "description='%s']",
                id,
                name,
                description
        );
    }
}