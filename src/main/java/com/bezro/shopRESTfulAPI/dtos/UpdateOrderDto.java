package com.bezro.shopRESTfulAPI.dtos;

import com.bezro.shopRESTfulAPI.entities.OrderStatus;
import com.bezro.shopRESTfulAPI.validation.OrderStatusConstraint;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateOrderDto {
    @NotNull(message = "The status is required.")
    @OrderStatusConstraint(message = "Invalid order status.")
    private String status;
}
