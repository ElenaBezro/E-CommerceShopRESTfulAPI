package com.bezro.shopRESTfulAPI.dtos;

import com.bezro.shopRESTfulAPI.entities.OrderItem;
import com.bezro.shopRESTfulAPI.entities.OrderStatus;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Data
@NoArgsConstructor
public class OrderResponse {
    private Long id;

    private Long UserId;

    private Instant createdAt;

    private OrderStatus status;

    private Double totalPrice;

    private List<OrderItem> orderItems;
}
