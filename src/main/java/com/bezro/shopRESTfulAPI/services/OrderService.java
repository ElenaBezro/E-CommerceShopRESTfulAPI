package com.bezro.shopRESTfulAPI.services;

import com.bezro.shopRESTfulAPI.dtos.OrderResponse;

import java.security.Principal;
import java.util.List;

public interface OrderService {
    OrderResponse createOrder(String userName);

    OrderResponse updateOrderStatus(Long id, String userName);

    List<OrderResponse> getAllOrders(String userName);

    //TODO:
//    Order getOrderById(Long id, Principal principal);
}