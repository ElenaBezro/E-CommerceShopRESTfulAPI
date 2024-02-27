package com.bezro.shopRESTfulAPI.services;

import com.bezro.shopRESTfulAPI.dtos.OrderResponse;
import com.bezro.shopRESTfulAPI.dtos.UpdateOrderDto;

import java.util.List;

public interface OrderService {
    OrderResponse createOrder(String userName);

    OrderResponse updateOrderStatus(UpdateOrderDto updateOrderDto, Long orderId);

    List<OrderResponse> getAllOrders(String userName);

    //TODO:
//    Order getOrderById(Long id, Principal principal);
}