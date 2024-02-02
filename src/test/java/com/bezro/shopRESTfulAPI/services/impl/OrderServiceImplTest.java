package com.bezro.shopRESTfulAPI.services.impl;

import com.bezro.shopRESTfulAPI.dtos.OrderResponse;
import com.bezro.shopRESTfulAPI.entities.*;
import com.bezro.shopRESTfulAPI.exceptions.EmptyCartException;
import com.bezro.shopRESTfulAPI.exceptions.NotEnoughProductStockException;
import com.bezro.shopRESTfulAPI.repositories.OrderRepository;
import com.bezro.shopRESTfulAPI.services.CartService;
import com.bezro.shopRESTfulAPI.services.OrderItemService;
import com.bezro.shopRESTfulAPI.services.ProductService;
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
    private CartService cartService;

    @Mock
    private ProductService productService;

    @Mock
    private UserService userService;

    @Mock
    private OrderItemService orderItemService;

    @InjectMocks
    private OrderServiceImpl orderService;

    @AfterEach
    void tearDown() {
        reset(orderRepository);
    }

    private static List<Order> getMockOrders(UserDetails mockUserDetails, Instant mockInstant, List<OrderItem> mockOrderItems) {
        List<Order> mockOrders = new ArrayList<>();

        Order order = getMockOrder(mockUserDetails, mockInstant, mockOrderItems);
        mockOrders.add(order);

        return mockOrders;
    }

    private static Order getMockOrder(UserDetails mockUserDetails, Instant mockInstant, List<OrderItem> mockOrderItems) {
        Order mockOrder = new Order();
        mockOrder.setId(30L);
        mockOrder.setUser((User) mockUserDetails);
        mockOrder.setCreatedAt(mockInstant);
        mockOrder.setStatus(OrderStatus.PROCESSING);
        mockOrder.setOrderItems(mockOrderItems);

        return mockOrder;
    }

    private static OrderResponse getMockOrderResponse(Instant mockInstant, List<OrderItem> mockOrderItems) {
        OrderResponse mockOrderResponse = new OrderResponse();
        mockOrderResponse.setId(30L);
        mockOrderResponse.setUserId(1L);
        mockOrderResponse.setCreatedAt(mockInstant);
        mockOrderResponse.setTotalPrice(35.0);
        mockOrderResponse.setStatus(OrderStatus.PROCESSING);
        mockOrderResponse.setOrderItems(mockOrderItems);

        return mockOrderResponse;
    }

    private static CartItem getMockCartItem(Long cartItemId, Long productId, UserDetails user) {
        CartItem mockCartItem = new CartItem();
        mockCartItem.setQuantity(7.0);
        mockCartItem.setId(cartItemId);
        mockCartItem.setUser((User) user);
        Product mockProduct1 = new Product();
        mockProduct1.setId(productId);
        mockProduct1.setQuantity(7.0);
        mockProduct1.setName("T-shirt");
        mockProduct1.setDescription("Red");
        mockProduct1.setPrice(5.0);
        mockCartItem.setProduct(mockProduct1);

        return mockCartItem;
    }

    private static OrderItem getMockOrderItem(Long orderItemId, Product product) {
        OrderItem mockOrderItem = new OrderItem();
        mockOrderItem.setId(orderItemId);
        mockOrderItem.setPrice(5.0);
        mockOrderItem.setQuantity(7.0);
        mockOrderItem.setProduct(product);

        return mockOrderItem;
    }

    private static List<CartItem> getMockCartItems() {
        CartItem mockCartItem1 = getMockCartItem(20L, 200L, null);
        CartItem mockCartItem2 = getMockCartItem(21L, 201L, null);

        return List.of(mockCartItem1, mockCartItem2);
    }

    private void assertOrderResponse(OrderResponse response, OrderResponse expectedOrderResponse) {
        assertEquals(expectedOrderResponse.getId(), response.getId(), "Ids should match");
        assertEquals(expectedOrderResponse.getUserId(), response.getUserId(), "UserIds should match");
        assertEquals(expectedOrderResponse.getCreatedAt(), response.getCreatedAt(), "Timestamps should match");
        assertEquals(expectedOrderResponse.getStatus(), response.getStatus(), "Statuses should match");
        assertEquals(expectedOrderResponse.getTotalPrice(), response.getTotalPrice(), "Total price should match");

        List<OrderItem> responseOrderItems = response.getOrderItems();
        List<OrderItem> expectedOrderItems = response.getOrderItems();

        assertNotNull(responseOrderItems, "OrderItems should not be null");
        assertSame(expectedOrderItems, responseOrderItems, "Should be the same object reference");
        assertEquals(expectedOrderItems.size(), responseOrderItems.size(), "Should have the same size");

        OrderItem firstOrderItem = responseOrderItems.get(0);
        OrderItem expectedFirstOrderItem = responseOrderItems.get(0);

        assertEquals(expectedFirstOrderItem.getPrice(), firstOrderItem.getPrice(), "Prices should match");
        assertEquals(expectedFirstOrderItem.getQuantity(), firstOrderItem.getQuantity(), "Quantities should match");

        assertNotNull(firstOrderItem.getProduct(), "Product should not be null");
    }

    @Test
    void shouldReturnOrderResponse_whenCreateOrder() {
        //Arrange
        UserDetails mockUserDetails = mock(User.class);

        long mockTimestampMillis = Instant.parse("2024-01-25T12:00:00Z").toEpochMilli();
        Instant mockInstant = Instant.ofEpochMilli(mockTimestampMillis);

        CartItem mockCartItem = getMockCartItem(20L, 200L, mockUserDetails);
        List<CartItem> mockCartItems = List.of(mockCartItem);

        OrderItem mockOrderItem = getMockOrderItem(10L, mockCartItem.getProduct());
        List<OrderItem> mockOrderItems = List.of(mockOrderItem);

        Order mockOrder = getMockOrder(mockUserDetails, mockInstant, mockOrderItems);

        OrderResponse expectedOrderResponse = getMockOrderResponse(mockInstant, mockOrderItems);

        when(((User) mockUserDetails).getId()).thenReturn(1L);
        when(userService.findByUsername("User")).thenReturn(mockUserDetails);
        when(cartService.getAllCartItems(1L)).thenReturn(mockCartItems);
        when(orderRepository.save(any())).thenReturn(mockOrder);
        when(orderItemService.getAllOrderItems(any())).thenReturn(mockOrderItems);
        doNothing().when(orderItemService).createOrderItem(any(), any());
        doNothing().when(cartService).removeCartItem(any());
        doNothing().when(productService).decreaseProductStock(200L, 7);

        //Act
        OrderResponse response = orderService.createOrder("User");

        //Assert
        assertOrderResponse(response, expectedOrderResponse);
        verify(cartService, times(1)).getAllCartItems(1L);
        verify(orderRepository, times(1)).save(any());
        verify(orderItemService, atLeast(1)).createOrderItem(any(), any());
        verify(cartService, atLeast(1)).removeCartItem(any());
        verify(productService, atLeast(1)).decreaseProductStock(200L, 7);
        verify(orderItemService, times(1)).getAllOrderItems(any());
    }

    @Test
    void shouldThrowEmptyCartException_whenCartIsEmpty() {
        // Arrange
//        Principal mockPrincipal = mock(Principal.class);
        UserDetails mockUserDetails = mock(User.class);

        when(((User) mockUserDetails).getId()).thenReturn(1L);
        when(userService.findByUsername(eq("User"))).thenReturn(mockUserDetails);
        when(cartService.getAllCartItems(1L)).thenReturn(new ArrayList<>());

        // Act
        // Assert
        EmptyCartException exception = assertThrows(EmptyCartException.class,
                () -> orderService.createOrder("User"),
                "When the user does not have any items in the cart, then throw EmptyCartException.");
        assertEquals(exception.getMessage(), "Cannot create order with an empty cart", "Should have the same exception message");
        verify(cartService, times(1)).getAllCartItems(1L);
    }

    @Test
    void shouldReturnVoid_whenConvertCartItemsIntoOrderItems() {
        //Arrange
        List<CartItem> mockCartItems = getMockCartItems();

        OrderItem mockOrderItem1 = getMockOrderItem(10L, mockCartItems.get(0).getProduct());
        OrderItem mockOrderItem2 = getMockOrderItem(11L, mockCartItems.get(1).getProduct());
        List<OrderItem> mockOrderItems = List.of(mockOrderItem1, mockOrderItem2);

        Order mockOrder = new Order();
        mockOrder.setOrderItems(mockOrderItems);

        doNothing().when(orderItemService).createOrderItem(any(), any());
        doNothing().when(cartService).removeCartItem(any());
        doNothing().when(productService).decreaseProductStock(any(Long.class), any(Double.class));

        //Act
        orderService.convertCartItemsIntoOrderItems(mockCartItems, mockOrder);

        //Assert
        verify(orderItemService, times(mockCartItems.size())).createOrderItem(any(), any());
        verify(cartService, times(mockCartItems.size())).removeCartItem(any());
        verify(productService, times(mockCartItems.size())).decreaseProductStock(any(Long.class), any(Double.class));
    }

    @Test
    void shouldReturnVoid_whenProductStockQuantityIsSufficient() {
        //Arrange
        CartItem mockCartItem = getMockCartItem(20L, 200L, null);
        List<CartItem> mockCartItems = List.of(mockCartItem);

        //Act & Assert
        assertDoesNotThrow(() -> orderService.validateProductStockQuantity(mockCartItems));
    }

    @Test
    void shouldThrowNotEnoughProductStockException_whenProductStockQuantityIsNotSufficient() {
        //Arrange
        double largeQuantity = 777;
        List<CartItem> mockCartItems = getMockCartItems();
        mockCartItems.get(0).setQuantity(largeQuantity);
        mockCartItems.get(1).setQuantity(largeQuantity);

        //Act & Assert
        NotEnoughProductStockException exception = assertThrows(NotEnoughProductStockException.class,
                () -> orderService.validateProductStockQuantity(mockCartItems),
                "When product stock quantity is not sufficient, then throw NotEnoughProductStockException.");
        assertEquals(2, exception.getErrors().size(), "Should have the same count of error messages");
    }

    @Test
    void shouldReturnOrdersResponse_whenGetAllOrders() {
        //Arrange
        UserDetails mockUserDetails = mock(User.class);

        long mockTimestampMillis = Instant.parse("2024-01-25T12:00:00Z").toEpochMilli();
        Instant mockInstant = Instant.ofEpochMilli(mockTimestampMillis);

        OrderItem mockOrderItem = getMockOrderItem(10L, new Product());
        List<OrderItem> mockOrderItems = List.of(mockOrderItem);

        List<Order> mockOrders = getMockOrders(mockUserDetails, mockInstant, mockOrderItems);

        when(((User) mockUserDetails).getId()).thenReturn(1L);
        when(userService.findByUsername("User")).thenReturn(mockUserDetails);
        when(orderRepository.findAllByUser_Id(1L)).thenReturn(mockOrders);

        //Act
        List<OrderResponse> response = orderService.getAllOrders("User");

        //Assert
        assertEquals(1, response.size(), "Should have the same size");

        OrderResponse firstOrderResponse = response.get(0);

        assertEquals(30, firstOrderResponse.getId(), "Ids should match");
        assertEquals(1, firstOrderResponse.getUserId(), "UserIds should match");
        assertEquals(mockInstant, firstOrderResponse.getCreatedAt(), "Timestamps should match");
        assertEquals(OrderStatus.PROCESSING, firstOrderResponse.getStatus(), "Statuses should match");
        assertEquals(35, firstOrderResponse.getTotalPrice(), "Total price should match");

        List<OrderItem> responseOrderItems = firstOrderResponse.getOrderItems();

        assertNotNull(responseOrderItems, "OrderItems should not be null");
        assertSame(mockOrderItems, responseOrderItems, "Should be the same object reference");
        assertEquals(1, responseOrderItems.size(), "Should have the same size");

        OrderItem firstOrderItem = responseOrderItems.get(0);

        assertEquals(5, firstOrderItem.getPrice(), "Prices should match");
        assertEquals(7, firstOrderItem.getQuantity(), "Quantities should match");

        assertNotNull(firstOrderItem.getProduct(), "Product should not be null");
    }

    @Test
    void shouldReturnEmptyList_whenUserDoesNotHaveOrders() {
        // Arrange
        UserDetails mockUserDetails = mock(User.class);

        when(((User) mockUserDetails).getId()).thenReturn(1L);
        when(userService.findByUsername(eq("User"))).thenReturn(mockUserDetails);
        when(orderRepository.findAllByUser_Id(eq(1L))).thenReturn(new ArrayList<>());

        // Act
        List<OrderResponse> response = orderService.getAllOrders("User");

        //Assert
        assertTrue(response.isEmpty(), "Should return an empty list");
    }

    @Test
    void shouldReturnOrderResponseObject_whenOrderItemsPassedAsParameter() {
        // Arrange
        UserDetails mockUserDetails = mock(User.class);

        long mockTimestampMillis = Instant.parse("2024-01-25T12:00:00Z").toEpochMilli();
        Instant mockInstant = Instant.ofEpochMilli(mockTimestampMillis);

        OrderItem mockOrderItem = getMockOrderItem(10L, new Product());
        List<OrderItem> mockOrderItems = List.of(mockOrderItem);

        Order mockOrder = getMockOrder(mockUserDetails, mockInstant, mockOrderItems);

        OrderResponse expectedOrderResponse = getMockOrderResponse(mockInstant, mockOrderItems);

        when(((User) mockUserDetails).getId()).thenReturn(1L);

        // Act
        OrderResponse response = orderService.createOrderResponse(mockOrder, mockOrderItems);

        //Assert
        assertOrderResponse(response, expectedOrderResponse);
    }

    @Test
    void shouldReturnOrderResponseObject_whenOrderItemsInParametersEqualsNull() {
        // Arrange
        UserDetails mockUserDetails = mock(User.class);

        long mockTimestampMillis = Instant.parse("2024-01-25T12:00:00Z").toEpochMilli();
        Instant mockInstant = Instant.ofEpochMilli(mockTimestampMillis);

        OrderItem mockOrderItem = getMockOrderItem(10L, new Product());
        List<OrderItem> mockOrderItems = List.of(mockOrderItem);

        Order mockOrder = getMockOrder(mockUserDetails, mockInstant, mockOrderItems);

        OrderResponse expectedOrderResponse = getMockOrderResponse(mockInstant, mockOrderItems);

        when(((User) mockUserDetails).getId()).thenReturn(1L);

        // Act
        OrderResponse response = orderService.createOrderResponse(mockOrder, null);

        //Assert
        assertOrderResponse(response, expectedOrderResponse);
    }

    @Test
    void shouldReturnTotalPrice_whenPassedOrderItems() {
        //Arrange
        OrderItem mockOrderItem1 = getMockOrderItem(10L, new Product());
        OrderItem mockOrderItem2 = getMockOrderItem(11L, new Product());
        List<OrderItem> mockOrderItems = List.of(mockOrderItem1, mockOrderItem2);

        //Act
        double totalPrice = orderService.calculateOrderTotalPrice(mockOrderItems);

        //Assert
        assertEquals(70.0, totalPrice, "Total price of order items should be the same");
    }
}
