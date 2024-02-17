package com.bezro.shopRESTfulAPI.exceptions;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class InsufficientProductStockException extends RuntimeException {
    public InsufficientProductStockException(String message) {
        super(message);
    }
}
