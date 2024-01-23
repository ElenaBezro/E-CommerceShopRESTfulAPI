package com.bezro.shopRESTfulAPI.dtos;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateCartItemDto {
    @NotNull(message = "The id of the product is required.")
    private Long productId;

    @NotNull(message = "The id of the user is required.")
    private Long userId;

    @NotNull(message = "The quantity is required.")
    @Min(value = 0, message = "Quantity must be positive")
    private double quantity;
}