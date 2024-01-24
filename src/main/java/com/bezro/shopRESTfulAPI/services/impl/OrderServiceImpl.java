package com.bezro.shopRESTfulAPI.services.impl;

import com.bezro.shopRESTfulAPI.entities.Order;
import com.bezro.shopRESTfulAPI.entities.OrderStatus;
import com.bezro.shopRESTfulAPI.entities.User;
import com.bezro.shopRESTfulAPI.repositories.OrderRepository;
import com.bezro.shopRESTfulAPI.services.OrderItemService;
import com.bezro.shopRESTfulAPI.services.OrderService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.Instant;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final OrderItemService orderItemService;
    private final UserService userService;
    private final OrderStatus INITIAL_ORDER_STATUS = OrderStatus.PROCESSING;

    @Transactional
    public Order createOrder(Principal principal) {
        //Store order
        Order order = new Order();
        User user = (User) userService.findByUsername(principal.getName());
        order.setUser(user);
        order.setCreatedAt(Instant.now());
        order.setStatus(INITIAL_ORDER_STATUS);
        Order orderStored = orderRepository.save(order);

        //Store order items
        convertCartItemsIntoOrderItems(user.getId(), orderStored.getId());

        return orderStored;
    }

    private void convertCartItemsIntoOrderItems(Long userId, Long orderId) {
        //TODO: get all user Cart items -> create and store Order items -> delete Cart items

    }

}
