package com.example.demo.entity;

import org.springframework.security.core.GrantedAuthority;

// 0 -> admin
// 1 -> client
public enum Role implements GrantedAuthority {
    ROLE_ADMIN, ROLE_CLIENT;

    public String getAuthority() {
        return name();
    }
}
