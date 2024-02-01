package com.bezro.shopRESTfulAPI.services;

import com.bezro.shopRESTfulAPI.dtos.OrderResponse;

import java.security.Principal;
import java.util.List;

public interface OrderService {
    OrderResponse createOrder(Principal principal);

    OrderResponse updateOrderStatus(Long id, Principal principal);

    List<OrderResponse> getAllOrders(Principal principal);

    //TODO:
//    Order getOrderById(Long id, Principal principal);
}