package com.bezro.shopRESTfulAPI.exceptions;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class ExceptionResponse {
    private int status;
    private List<String> messages;
    private Date timestamp;

    public ExceptionResponse(int status, List<String> messages) {
        this.status = status;
        this.messages = messages;
        this.timestamp = new Date();
    }
}
