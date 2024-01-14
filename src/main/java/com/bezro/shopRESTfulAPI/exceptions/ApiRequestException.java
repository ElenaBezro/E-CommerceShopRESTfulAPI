package com.bezro.shopRESTfulAPI.exceptions;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class ApiRequestException {
    private int status;
    private List<String> messages;
    private Date timestamp;

    public ApiRequestException(int status, List<String> messages) {
        this.status = status;
        this.messages = messages;
        this.timestamp = new Date();
    }
}
