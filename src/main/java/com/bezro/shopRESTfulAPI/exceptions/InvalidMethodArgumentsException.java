package com.bezro.shopRESTfulAPI.exceptions;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class InvalidMethodArgumentsException extends RuntimeException {
    public InvalidMethodArgumentsException(String message) {
        super(message);
    }
}

