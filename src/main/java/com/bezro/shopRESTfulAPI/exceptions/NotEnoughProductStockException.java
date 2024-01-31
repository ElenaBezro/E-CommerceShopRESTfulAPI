package com.bezro.shopRESTfulAPI.exceptions;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

import java.util.List;


@EqualsAndHashCode(callSuper = true)
@Data
public class NotEnoughProductStockException extends RuntimeException {
    private List<String> errors;

    public NotEnoughProductStockException(List<String> errors) {
        this.errors = errors;
    }
}
