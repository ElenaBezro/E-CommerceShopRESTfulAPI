package com.bezro.shopRESTfulAPI.exceptions;

import lombok.Data;

@Data
public class UserAlreadyExistsException extends RuntimeException {

    public UserAlreadyExistsException(String message) {
        super(message);
    }
}

