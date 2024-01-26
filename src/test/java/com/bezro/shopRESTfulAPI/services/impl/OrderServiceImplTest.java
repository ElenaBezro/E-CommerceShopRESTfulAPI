package com.bezro.shopRESTfulAPI.services.impl;

import com.bezro.shopRESTfulAPI.dtos.OrderResponse;
import com.bezro.shopRESTfulAPI.entities.*;
import com.bezro.shopRESTfulAPI.exceptions.NoContentException;
import com.bezro.shopRESTfulAPI.repositories.OrderRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;

import java.security.Principal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {
    @Mock
    private OrderRepository orderRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private OrderServiceImpl orderService;

    @AfterEach
    void tearDown() {
        reset(orderRepository);
    }

    private List<Order> mockOrders(UserDetails mockUserDetails, Instant mockInstant, List<OrderItem> mockOrderItems) {
        List<Order> mockOrders = new ArrayList<>();

        Order order = new Order();
        order.setId(1L);
        order.setUser((User) mockUserDetails);
        order.setCreatedAt(mockInstant);
        order.setStatus(OrderStatus.PROCESSING);
        order.setOrderItems(mockOrderItems);
        mockOrders.add(order);

        return mockOrders;
    }

    @Test
    void shouldReturnOrdersResponse_whenGetAllOrders() {
        //Arrange
        Principal mockPrincipal = mock(Principal.class);
        UserDetails mockUserDetails = mock(User.class);

        long mockTimestampMillis = Instant.parse("2024-01-25T12:00:00Z").toEpochMilli();
        Instant mockInstant = Instant.ofEpochMilli(mockTimestampMillis);

        OrderItem mockOrderItem = new OrderItem();
        mockOrderItem.setPrice(2);
        mockOrderItem.setQuantity(10);
        Product mockProduct = new Product();
        mockOrderItem.setProduct(mockProduct);

        List<OrderItem> mockOrderItems = List.of(mockOrderItem);

        List<Order> mockOrders = mockOrders(mockUserDetails, mockInstant, mockOrderItems);

        when(((User) mockUserDetails).getId()).thenReturn(1L);
        when(mockPrincipal.getName()).thenReturn("User");
        when(userService.findByUsername("User")).thenReturn(mockUserDetails);
        when(orderRepository.findAllByUser_Id(1L)).thenReturn(mockOrders);

        //Act
        List<OrderResponse> response = orderService.getAllOrders(mockPrincipal);

        //Assert
        assertEquals(1, response.size(), "Should have the same size");

        OrderResponse firstOrderResponse = response.get(0);

        assertEquals(1, firstOrderResponse.getId(), "Ids should match");
        assertEquals(1, firstOrderResponse.getUserId(), "UserIds should match");
        assertEquals(mockInstant, firstOrderResponse.getCreatedAt(), "Timestamps should match");
        assertEquals(OrderStatus.PROCESSING, firstOrderResponse.getStatus(), "Statuses should match");

        List<OrderItem> responseOrderItems = firstOrderResponse.getOrderItems();

        assertNotNull(responseOrderItems, "OrderItems should not be null");
        assertSame(mockOrderItems, responseOrderItems, "Should be the same object reference");
        assertEquals(1, responseOrderItems.size(), "Should have the same size");

        OrderItem firstOrderItem = responseOrderItems.get(0);

        assertEquals(2, firstOrderItem.getPrice(), "Prices should match");
        assertEquals(10, firstOrderItem.getQuantity(), "Quantities should match");

        assertEquals(20, firstOrderResponse.getTotalPrice(), "Total price should match");
        assertNotNull(firstOrderItem.getProduct(), "Product should not be null");
    }

    @Test
    void shouldThrowNoContent_whenUserDoesNotHaveOrders() {
        // Arrange
        Principal mockPrincipal = mock(Principal.class);
        UserDetails mockUserDetails = mock(User.class);

        when(((User) mockUserDetails).getId()).thenReturn(1L);
        when(mockPrincipal.getName()).thenReturn("User");
        when(userService.findByUsername(eq("User"))).thenReturn(mockUserDetails);
        when(orderRepository.findAllByUser_Id(eq(1L))).thenReturn(new ArrayList<>());

        // Act
        // Assert
        NoContentException exception = assertThrows(NoContentException.class,
                () -> orderService.getAllOrders(mockPrincipal),
                "When the user does not have any orders, then throw NoContentException.");
        assertEquals(exception.getMessage(), "No Content", "Should have the same exception message");
        verify(orderRepository, times(1)).findAllByUser_Id(1L);
    }
}
