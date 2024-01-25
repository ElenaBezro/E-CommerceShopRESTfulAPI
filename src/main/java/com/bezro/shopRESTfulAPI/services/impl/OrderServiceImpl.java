package com.bezro.shopRESTfulAPI.services.impl;

import com.bezro.shopRESTfulAPI.entities.CartItem;
import com.bezro.shopRESTfulAPI.entities.Order;
import com.bezro.shopRESTfulAPI.entities.OrderStatus;
import com.bezro.shopRESTfulAPI.entities.User;
import com.bezro.shopRESTfulAPI.exceptions.ChangeFinalOrderStatusException;
import com.bezro.shopRESTfulAPI.exceptions.InvalidMethodArgumentsException;
import com.bezro.shopRESTfulAPI.exceptions.NoContentException;
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
    private final OrderStatus FINAL_ORDER_STATUS = OrderStatus.DELIVERED;

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
        //TODO: throw exception No content if cartItemList is empty
        cartItemList.forEach(cartItem -> {
            orderItemService.createOrderItem(cartItem, order);
            cartService.removeCartItem(cartItem.getId());
        });
    }

    private Order findById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new InvalidMethodArgumentsException(
                        String.format("Order with id: %d does not exist", id)));
    }

    public Order updateOrderStatus(Long id, Principal principal) {
        //TODO: check that principal has ADMIN role
        Order order = findById(id);
        OrderStatus previousStatus = order.getStatus();
        if (previousStatus.equals(FINAL_ORDER_STATUS)) {
            throw new ChangeFinalOrderStatusException("Order has final status. Sorry, you cannot go back in time.");
        }
        OrderStatus newStatus = OrderStatus.getNext(previousStatus);
        order.setStatus(newStatus);
        return orderRepository.save(order);
    }

    public List<Order> getAllOrders(Principal principal) {
        User user = (User) userService.findByUsername(principal.getName());
        Long userId = user.getId();

        List<Order> orders = orderRepository.findAllByUser_Id(userId);
        if (orders.isEmpty()) {
            throw new NoContentException("No Content");
        }

        return orders;
    }
}
