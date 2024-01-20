package com.bezro.shopRESTfulAPI.exceptions;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class NoContentException extends RuntimeException {
    public NoContentException(String message) {
        super(message);
    }
}
