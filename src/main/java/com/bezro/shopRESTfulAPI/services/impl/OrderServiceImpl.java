package com.bezro.shopRESTfulAPI.services.impl;

import com.bezro.shopRESTfulAPI.dtos.OrderResponse;
import com.bezro.shopRESTfulAPI.entities.*;
import com.bezro.shopRESTfulAPI.exceptions.ChangeFinalOrderStatusException;
import com.bezro.shopRESTfulAPI.exceptions.EmptyCartException;
import com.bezro.shopRESTfulAPI.exceptions.InvalidMethodArgumentsException;
import com.bezro.shopRESTfulAPI.exceptions.NotEnoughProductStockException;
import com.bezro.shopRESTfulAPI.repositories.OrderRepository;
import com.bezro.shopRESTfulAPI.services.CartService;
import com.bezro.shopRESTfulAPI.services.OrderItemService;
import com.bezro.shopRESTfulAPI.services.OrderService;
import com.bezro.shopRESTfulAPI.services.ProductService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final OrderItemService orderItemService;
    private final UserService userService;
    private final CartService cartService;
    private final ProductService productService;

    private final OrderStatus INITIAL_ORDER_STATUS = OrderStatus.PROCESSING;
    private final OrderStatus FINAL_ORDER_STATUS = OrderStatus.DELIVERED;

    @Transactional
    public OrderResponse createOrder(String userName) {
        User user = (User) userService.findByUsername(userName);
        List<CartItem> cartItemList = cartService.getAllCartItems(user.getId());
        if (cartItemList.isEmpty()) {
            throw new EmptyCartException("Cannot create order with an empty cart");
        }

        validateProductStockQuantity(cartItemList);
        //Store order
        Order order = new Order();
        order.setUser(user);
        order.setCreatedAt(Instant.now());
        order.setStatus(INITIAL_ORDER_STATUS);
        Order orderStored = orderRepository.save(order);

        //Store order items
        convertCartItemsIntoOrderItems(cartItemList, orderStored);

        List<OrderItem> orderItems = orderItemService.getAllOrderItems(orderStored.getId());

        return createOrderResponse(orderStored, orderItems);
    }

    @Transactional
    public void convertCartItemsIntoOrderItems(List<CartItem> cartItemList, Order order) {
        cartItemList.forEach(cartItem -> {
            orderItemService.createOrderItem(cartItem, order);
            cartService.removeCartItem(cartItem.getId());
            productService.decreaseProductStock(cartItem.getProduct().getId(), cartItem.getQuantity());
        });
    }

    public void validateProductStockQuantity(List<CartItem> cartItemList) {
        List<String> errors = new ArrayList<>();
        cartItemList.forEach(cartItem -> {
            double productStock = cartItem.getProduct().getQuantity();
            if (productStock < cartItem.getQuantity()) {
                errors.add("Not enough product with id: " + cartItem.getProduct().getId());
            }
        });
        if (!errors.isEmpty()) {
            throw new NotEnoughProductStockException(errors);
        }
    }

    private Order findById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new InvalidMethodArgumentsException(
                        String.format("Order with id: %d does not exist", id)));
    }

    public OrderResponse updateOrderStatus(Long id, String userName) {
        //TODO: check that principal has ADMIN role
        //TODO: change method to accept newStatus value
        //TODO: add test for this method
        Order order = findById(id);
        OrderStatus previousStatus = order.getStatus();
        if (previousStatus.equals(FINAL_ORDER_STATUS)) {
            throw new ChangeFinalOrderStatusException("Order has final status. Sorry, you cannot go back in time.");
        }
        OrderStatus newStatus = OrderStatus.getNext(previousStatus);
        order.setStatus(newStatus);
        Order storedOrder = orderRepository.save(order);
        return createOrderResponse(storedOrder, null);
    }

    public List<OrderResponse> getAllOrders(String userName) {
        User user = (User) userService.findByUsername(userName);
        Long userId = user.getId();

        List<Order> orders = orderRepository.findAllByUser_Id(userId);

        return orders.stream().map(order -> {
            OrderResponse orderResponse = new OrderResponse();
            orderResponse.setId(order.getId());
            orderResponse.setUserId(userId);
            orderResponse.setCreatedAt(order.getCreatedAt());
            orderResponse.setStatus(order.getStatus());
            List<OrderItem> orderItems = order.getOrderItems();
            orderResponse.setOrderItems(orderItems);
            double totalPrice = orderItems.stream()
                    .mapToDouble(orderItem -> orderItem.getPrice() * orderItem.getQuantity())
                    .sum();
            orderResponse.setTotalPrice(totalPrice);
            return orderResponse;
        }).toList();
    }

    public OrderResponse createOrderResponse(Order order, List<OrderItem> orderItems) {
        OrderResponse orderResponse = new OrderResponse();
        orderResponse.setId(order.getId());
        orderResponse.setUserId(order.getUser().getId());
        orderResponse.setCreatedAt(order.getCreatedAt());
        orderResponse.setStatus(order.getStatus());
        List<OrderItem> orderItemsToStore = orderItems == null ? order.getOrderItems() : orderItems;
        orderResponse.setOrderItems(orderItemsToStore);
        orderResponse.setTotalPrice(calculateOrderTotalPrice(orderItemsToStore));
        return orderResponse;
    }

    public double calculateOrderTotalPrice(List<OrderItem> orderItems) {
        return orderItems.stream()
                .mapToDouble(orderItem -> orderItem.getPrice() * orderItem.getQuantity())
                .sum();
    }

}
