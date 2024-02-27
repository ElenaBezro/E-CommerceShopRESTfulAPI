package com.bezro.shopRESTfulAPI.services.impl;

import com.bezro.shopRESTfulAPI.entities.CartItem;
import com.bezro.shopRESTfulAPI.entities.Order;
import com.bezro.shopRESTfulAPI.entities.OrderItem;
import com.bezro.shopRESTfulAPI.entities.Product;
import com.bezro.shopRESTfulAPI.exceptions.NoContentException;
import com.bezro.shopRESTfulAPI.repositories.OrderItemRepository;
import com.bezro.shopRESTfulAPI.services.OrderItemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderItemServiceImpl implements OrderItemService {
    private final OrderItemRepository orderItemRepository;

    public void createOrderItem(CartItem cartItem, Order order) {
        log.info("Creating order item for cart item: {}", cartItem.getId());
        OrderItem orderItem = new OrderItem();
        orderItem.setOrder(order);
        Product product = cartItem.getProduct();
        orderItem.setProduct(product);
        orderItem.setPrice(product.getPrice());
        orderItem.setQuantity(cartItem.getQuantity());

        orderItemRepository.save(orderItem);
        log.info("Order item created successfully: {}", orderItem.getId());
    }

    public List<OrderItem> getAllOrderItems(Long orderId) {
        log.info("Getting all order items for order: {}", orderId);
        List<OrderItem> orderItems = orderItemRepository.findAllByOrder_Id(orderId);
        if (orderItems.isEmpty()) {
            log.info("No order items found for order: {}", orderId);
            throw new NoContentException("No Content");
        }

        log.info("Found {} order items for order: {}", orderItems.size(), orderId);
        return orderItems;
    }
}
