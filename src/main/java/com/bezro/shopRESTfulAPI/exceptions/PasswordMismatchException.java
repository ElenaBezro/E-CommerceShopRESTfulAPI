package com.bezro.shopRESTfulAPI.exceptions;

import lombok.Data;

@Data
public class PasswordMismatchException extends RuntimeException {

    public PasswordMismatchException(String message) {
        super(message);
    }
}
