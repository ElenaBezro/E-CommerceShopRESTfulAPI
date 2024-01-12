package com.bezro.shopRESTfulAPI.exceptions;

import lombok.Data;

@Data
public class InvalidLoginCredentialsException extends RuntimeException {

    public InvalidLoginCredentialsException(String message) {
        super(message);
    }
}
