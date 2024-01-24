package com.bezro.shopRESTfulAPI.services;

import com.bezro.shopRESTfulAPI.entities.OrderItem;

import java.security.Principal;
import java.util.List;

public interface OrderItemService {
    OrderItem createOrderItem(Long product_id, Principal principal);
    List<OrderItem> getAllOrderItems(Principal principal);
}