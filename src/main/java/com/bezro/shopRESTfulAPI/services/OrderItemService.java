package com.bezro.shopRESTfulAPI.services;

import com.bezro.shopRESTfulAPI.entities.CartItem;
import com.bezro.shopRESTfulAPI.entities.Order;
import com.bezro.shopRESTfulAPI.entities.OrderItem;

import java.util.List;

public interface OrderItemService {
    void createOrderItem(CartItem cartItem, Order order);

    List<OrderItem> getAllOrderItems(Long orderId);
}