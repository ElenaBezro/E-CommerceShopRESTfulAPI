package com.bezro.shopRESTfulAPI.exceptions;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class ChangeFinalOrderStatusException extends RuntimeException {
    public ChangeFinalOrderStatusException(String message) {
        super(message);
    }
}
