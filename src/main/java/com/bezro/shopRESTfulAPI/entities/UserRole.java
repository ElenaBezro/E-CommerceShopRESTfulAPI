package com.bezro.shopRESTfulAPI.entities;

import lombok.Getter;

@Getter
public enum UserRole {
    USER("ROLE_USER"),
    ADMIN("ROLE_ADMIN");

    private final String name;

    UserRole(String name) {
        this.name = name;
    }

}
