package com.bezro.shopRESTfulAPI.services.impl;

import com.bezro.shopRESTfulAPI.entities.*;
import com.bezro.shopRESTfulAPI.repositories.OrderRepository;
import com.bezro.shopRESTfulAPI.services.CartService;
import com.bezro.shopRESTfulAPI.services.OrderItemService;
import com.bezro.shopRESTfulAPI.services.OrderService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final OrderItemService orderItemService;
    private final UserService userService;
    private final CartService cartService;
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
        convertCartItemsIntoOrderItems(user.getId(), orderStored);

        return orderStored;
    }

    @Transactional
    private void convertCartItemsIntoOrderItems(Long userId, Order order) {
        List<CartItem> cartItemList = cartService.getAllCartItems(userId);
        cartItemList.forEach(cartItem -> {
            orderItemService.createOrderItem(cartItem, order);
            cartService.removeCartItem(cartItem.getId());
        });
    }

}
