package com.bezro.shopRESTfulAPI.exceptions;

import lombok.Data;

import java.util.Date;

@Data
//TODO: consider using custom Exception
public class AuthException {
    private int status;
    private String message;
    private Date timestamp;

    public AuthException(int status, String message) {
        this.status = status;
        this.message = message;
        this.timestamp = new Date();
    }
}
