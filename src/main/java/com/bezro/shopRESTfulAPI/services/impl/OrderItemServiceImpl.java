package com.bezro.shopRESTfulAPI.services.impl;

import com.bezro.shopRESTfulAPI.entities.OrderItem;
import com.bezro.shopRESTfulAPI.repositories.OrderItemRepository;
import com.bezro.shopRESTfulAPI.services.OrderItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderItemServiceImpl implements OrderItemService {
    private final OrderItemRepository orderItemRepository;
    private final UserService userService;

    //TODO: implement createOrderItem
    public OrderItem createOrderItem(Long product_id, Principal principal) {
        return new OrderItem();
    }

    //TODO: implement getAllOrderItems
    public List<OrderItem> getAllOrderItems(Principal principal) {
        return new ArrayList<>();
    }
}
