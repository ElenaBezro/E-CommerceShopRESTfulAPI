package com.bezro.shopRESTfulAPI.services.impl;

import com.bezro.shopRESTfulAPI.entities.*;
import com.bezro.shopRESTfulAPI.exceptions.NoContentException;
import com.bezro.shopRESTfulAPI.repositories.OrderItemRepository;
import com.bezro.shopRESTfulAPI.services.OrderItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderItemServiceImpl implements OrderItemService {
    private final OrderItemRepository orderItemRepository;

    public void createOrderItem(CartItem cartItem, Order order) {
        //TODO: return OrderItem
        OrderItem orderItem = new OrderItem();
        orderItem.setOrder(order);
        Product product = cartItem.getProduct();
        orderItem.setProduct(product);
        orderItem.setPrice(product.getPrice());
        orderItem.setQuantity(cartItem.getQuantity());

        OrderItemId orderItemId = new OrderItemId();
        orderItemId.setOrderId(order.getId());
        orderItemId.setProductId(product.getId());
        orderItem.setOrderItemId(orderItemId);

        orderItemRepository.save(orderItem);
    }

    public List<OrderItem> getAllOrderItems(Long orderId, Principal principal) {
        List<OrderItem> orderItems = orderItemRepository.findAllByOrder_Id(orderId);
        if (orderItems.isEmpty()) {
            throw new NoContentException("No Content");
        }

        return orderItems;
    }
}
