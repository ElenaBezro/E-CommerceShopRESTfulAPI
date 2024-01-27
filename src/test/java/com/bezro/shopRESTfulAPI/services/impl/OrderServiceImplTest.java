package com.bezro.shopRESTfulAPI.services.impl;

import com.bezro.shopRESTfulAPI.entities.Order;
import com.bezro.shopRESTfulAPI.entities.OrderStatus;
import com.bezro.shopRESTfulAPI.entities.User;
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

    @Test
    void shouldReturnListOfOrders_whenGetAllOrders() {
        //Arrange
        List<Order> mockOrders = new ArrayList<>();
        Principal mockPrincipal = mock(Principal.class);
        UserDetails mockUserDetails = mock(User.class);

        Order order = new Order();
        order.setId(1L);
        order.setUser((User) mockUserDetails);
        long mockTimestampMillis = Instant.parse("2024-01-25T12:00:00Z").toEpochMilli();
        Instant mockInstant = Instant.ofEpochMilli(mockTimestampMillis);
        order.setCreatedAt(mockInstant);
        order.setStatus(OrderStatus.PROCESSING);
        mockOrders.add(order);

        when(((User) mockUserDetails).getId()).thenReturn(1L);
        when(mockPrincipal.getName()).thenReturn("User");
        when(userService.findByUsername(eq("User"))).thenReturn(mockUserDetails);
        when(orderRepository.findAllByUser_Id(eq(1L))).thenReturn(mockOrders);

        //Act
        List<Order> response = orderService.getAllOrders(mockPrincipal);

        //Assert
        assertSame(mockOrders, response, "Should be the same object reference");
        assertEquals(mockOrders.size(), response.size(), "Should have the same size");
        assertEquals(1, response.get(0).getId(), "Ids should match");
        assertEquals(mockUserDetails, response.get(0).getUser(), "Should be the same object reference");
        assertEquals(mockInstant, response.get(0).getCreatedAt(), "Timestamps should match");
        assertEquals(OrderStatus.PROCESSING, response.get(0).getStatus(), "Statuses should match");
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
