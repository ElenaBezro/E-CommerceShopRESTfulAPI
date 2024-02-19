package com.bezro.shopRESTfulAPI.services.impl;

import com.bezro.shopRESTfulAPI.dtos.OrderResponse;
import com.bezro.shopRESTfulAPI.dtos.UpdateOrderDto;
import com.bezro.shopRESTfulAPI.entities.*;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final OrderItemService orderItemService;
    private final UserService userService;
    private final CartService cartService;
    private final ProductService productService;

    private final OrderStatus INITIAL_ORDER_STATUS = OrderStatus.PROCESSING;

    @Transactional
    public OrderResponse createOrder(String userName) {
        log.info("Creating order for user: {}", userName);
        User user = (User) userService.findByUsername(userName);
        List<CartItem> cartItemList = cartService.getAllCartItems(user.getId());
        if (cartItemList.isEmpty()) {
            log.info("Cannot create order with an empty cart for user: {}", userName);
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

        log.info("Order created successfully for user: {}", userName);
        return createOrderResponse(orderStored, orderItems);
    }

    @Transactional
    public void convertCartItemsIntoOrderItems(List<CartItem> cartItemList, Order order) {
        log.info("Converting cart items into order items for order: {}", order.getId());
        cartItemList.forEach(cartItem -> {
            orderItemService.createOrderItem(cartItem, order);
            cartService.removeCartItem(cartItem.getId());
            productService.decreaseProductStock(cartItem.getProduct().getId(), cartItem.getQuantity());
        });
        log.info("Cart items converted into order items for order: {}", order.getId());
    }

    public void validateProductStockQuantity(List<CartItem> cartItemList) {
        log.info("Validating product stock quantity for cart items");
        List<String> errors = new ArrayList<>();
        cartItemList.forEach(cartItem -> {
            double productStock = cartItem.getProduct().getQuantity();
            if (productStock < cartItem.getQuantity()) {
                errors.add("Not enough product with id: " + cartItem.getProduct().getId());
            }
        });
        if (!errors.isEmpty()) {
            log.info("Product stock quantity validation failed: {}", errors);
            throw new NotEnoughProductStockException(errors);
        }
        log.info("Product stock quantity validation successful");
    }

    public Order findById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new InvalidMethodArgumentsException(
                        String.format("Order with id: %d does not exist", id)));
    }

    public OrderResponse updateOrderStatus(UpdateOrderDto updateOrderDto, Long orderId) {
        log.info("Updating order status for order: {}", orderId);
        Order order = findById(orderId);
        String statusString = updateOrderDto.getStatus();
        OrderStatus orderStatus = OrderStatus.fromString(statusString);
        order.setStatus(orderStatus);
        Order storedOrder = orderRepository.save(order);
        log.info("Order status updated successfully for order: {}", orderId);
        return createOrderResponse(storedOrder, null);
    }

    public List<OrderResponse> getAllOrders(String userName) {
        log.info("Getting all orders for user: {}", userName);
        User user = (User) userService.findByUsername(userName);
        Long userId = user.getId();

        List<Order> orders = orderRepository.findAllByUser_Id(userId);

        log.info("Found {} orders for user: {}", orders.size(), userName);
        return orders.stream().map(order -> createOrderResponse(order, null)).toList();
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
