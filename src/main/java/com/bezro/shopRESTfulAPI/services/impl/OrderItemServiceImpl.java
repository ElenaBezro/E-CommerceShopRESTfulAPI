package com.bezro.shopRESTfulAPI.services.impl;

import com.bezro.shopRESTfulAPI.entities.CartItem;
import com.bezro.shopRESTfulAPI.entities.Order;
import com.bezro.shopRESTfulAPI.entities.OrderItem;
import com.bezro.shopRESTfulAPI.entities.Product;
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

    public void createOrderItem(CartItem cartItem, Order order) {
        //TODO: return OrderItem
        OrderItem orderItem = new OrderItem();
        orderItem.setOrder(order);
        Product product = cartItem.getProduct();
        orderItem.setProduct(product);
        orderItem.setPrice(product.getPrice());
        orderItem.setQuantity(cartItem.getQuantity());

        orderItemRepository.save(orderItem);
    }

    //TODO: implement getAllOrderItems
    public List<OrderItem> getAllOrderItems(Principal principal) {
        return new ArrayList<>();
    }
}
