package com.bezro.shopRESTfulAPI.entities;

import lombok.Data;
import lombok.Getter;

@Getter
public enum UserRole {
    USER("ROLE_USER"),
    ADMIN("ROLE_ADMIN");

    private String name;

    UserRole(String name) {
        this.name = name;
    }

}
