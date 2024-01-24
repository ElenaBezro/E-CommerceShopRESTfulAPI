package com.bezro.shopRESTfulAPI.services;

import com.bezro.shopRESTfulAPI.entities.Order;

import java.security.Principal;

public interface OrderService {
    Order createOrder(Principal principal);

    //TODO:
//    Order getOrderById(Long id, Principal principal);
//
//    Order updateOrderStatus (Long id, UpdateOrderDto updateOrderDto, Principal principal);
//
//    List<Order> getAllOrders(Principal principal);
}