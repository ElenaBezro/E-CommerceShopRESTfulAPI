package com.bezro.shopRESTfulAPI.exceptions;

import lombok.Data;

@Data
public class RoleNotFoundException extends RuntimeException {

    public RoleNotFoundException(String message) {
        super(message);
    }
}
