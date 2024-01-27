package com.bezro.shopRESTfulAPI.services;

import com.bezro.shopRESTfulAPI.entities.Order;

import java.security.Principal;
import java.util.List;

public interface OrderService {
    Order createOrder(Principal principal);

    Order updateOrderStatus(Long id, Principal principal);

    List<Order> getAllOrders(Principal principal);

    //TODO:
//    Order getOrderById(Long id, Principal principal);
}