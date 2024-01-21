package com.bezro.shopRESTfulAPI.exceptions;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class InvalidRequestParametersException extends RuntimeException {
    public InvalidRequestParametersException(String message) {
        super(message);
    }
}

