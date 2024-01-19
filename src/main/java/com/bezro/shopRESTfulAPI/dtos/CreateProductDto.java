package com.bezro.shopRESTfulAPI.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class CreateProductDto {
    @NotBlank(message = "The name is required.")
    private String name;

    private String description;

    @NotNull(message = "The price is required.")
    @Positive(message = "Price must be greater than zero")
    private double price;

    @NotNull(message = "The stock quantity is required.")
    @Positive(message = "Stock quantity must be greater than zero")
    private double quantity;
}
