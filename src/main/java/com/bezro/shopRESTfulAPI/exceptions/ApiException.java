package com.bezro.shopRESTfulAPI.exceptions;

import lombok.Data;

import java.util.Date;

@Data
public class ApiException {
    private int status;
    private String message;
    private Date timestamp;

    public ApiException(int status, String message) {
        this.status = status;
        this.message = message;
        this.timestamp = new Date();
    }
}
