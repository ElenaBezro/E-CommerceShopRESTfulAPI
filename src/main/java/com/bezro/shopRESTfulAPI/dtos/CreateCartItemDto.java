package com.bezro.shopRESTfulAPI.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class CreateCartItemDto {
    @NotNull(message = "The id of the product is required.")
    private Long productId;

    @NotNull(message = "The id of the user is required.")
    private Long userId;

    @NotNull(message = "The quantity is required.")
    @Positive(message = "Quantity must be positive")
    private double quantity;
}