package com.bezro.shopRESTfulAPI.services.impl;

import com.bezro.shopRESTfulAPI.dtos.CartItemResponse;
import com.bezro.shopRESTfulAPI.dtos.CreateCartItemDto;
import com.bezro.shopRESTfulAPI.entities.CartItem;
import com.bezro.shopRESTfulAPI.entities.Product;
import com.bezro.shopRESTfulAPI.entities.User;
import com.bezro.shopRESTfulAPI.exceptions.CartItemAlreadyExistsException;
import com.bezro.shopRESTfulAPI.exceptions.InsufficientProductStockException;
import com.bezro.shopRESTfulAPI.exceptions.InvalidMethodArgumentsException;
import com.bezro.shopRESTfulAPI.repositories.CartRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceImplTest {
    @Mock
    private CartRepository cartRepository;

    @Mock
    private ProductServiceImpl productService;

    @Mock
    private UserService userService;

    @InjectMocks
    private CartServiceImpl cartService;

    @AfterEach
    void tearDown() {
        reset(cartRepository);
    }

    private CreateCartItemDto createCartItemDto() {
        CreateCartItemDto createCartItemDto = new CreateCartItemDto();
        createCartItemDto.setUserId(1L);
        createCartItemDto.setQuantity(5.0);
        createCartItemDto.setProductId(1L);

        return createCartItemDto;
    }

    private Product productMock() {
        Product productMock = new Product();
        productMock.setId(1L);
        productMock.setName("ProductName");
        productMock.setDescription("ProductDescription");
        productMock.setPrice(2.0d);
        productMock.setQuantity(10.5d);

        return productMock;
    }

    private CartItem cartItemMock(UserDetails mockUserDetails, Product product) {
        CartItem cartItem = new CartItem();
        cartItem.setId(1L);
        cartItem.setUser((User) mockUserDetails);
        cartItem.setQuantity(5.0d);
        cartItem.setProduct(product);

        return cartItem;
    }

    @Test
    void shouldReturnCartItemResponse_whenAddNewCartItem() {
        // Arrange
        UserDetails mockUserDetails = mock(User.class);
        Product mockProduct = productMock();
        CreateCartItemDto createCartItemDto = createCartItemDto();
        CartItem cartItemMock = cartItemMock(mockUserDetails, mockProduct);


        when(((User) mockUserDetails).getId()).thenReturn(1L);
        when(userService.findByUsername(any())).thenReturn(mockUserDetails);
        when(productService.findById(1L)).thenReturn(mockProduct);
        when(cartRepository.findByProduct_IdAndUser_Id(any(), any())).thenReturn(Optional.empty());
        when(cartRepository.save(any(CartItem.class))).thenReturn(cartItemMock);

        // Act
        CartItemResponse cartItem = cartService.addCartItem(createCartItemDto, "User");

        // Assert
        assertEquals(cartItem.getId(), cartItemMock.getId(), "Ids should match");
        assertSame(cartItem.getProduct(), cartItemMock.getProduct(), "Should be the same object reference");
        assertEquals(cartItem.getUserId(), cartItemMock.getUser().getId(), "User ID should match");
        assertEquals(cartItem.getQuantity(), cartItemMock.getQuantity(), "Quantity should match");

        Product product = cartItem.getProduct();
        assertEquals(product.getId(), mockProduct.getId(), "Ids should match");
        assertEquals(product.getQuantity(), mockProduct.getQuantity(), "Quantity should match");
        assertEquals(product.getName(), mockProduct.getName(), "Name should match");
        assertEquals(product.getDescription(), mockProduct.getDescription(), "Description should match");
        assertEquals(product.getPrice(), mockProduct.getPrice(), "Price should match");
    }

    @Test
    void shouldThrow400CodeStatus_whenAddNewCartItemWithExistingCartItem() {
        // Arrange
        UserDetails mockUserDetails = mock(User.class);
        Product mockProduct = productMock();
        CartItem cartItemMock = cartItemMock(mockUserDetails, mockProduct);


        when(((User) mockUserDetails).getId()).thenReturn(1L);
        when(userService.findByUsername(any())).thenReturn(mockUserDetails);
        when(productService.findById(1L)).thenReturn(mockProduct);
        when(cartRepository.findByProduct_IdAndUser_Id(any(), any())).thenReturn(Optional.of(cartItemMock));

        // Act
        // Assert
        CartItemAlreadyExistsException exception = assertThrows(CartItemAlreadyExistsException.class,
                () -> cartService.addCartItem(createCartItemDto(), "User"),
                "Adding to the cart already existing cart item should throw CartItemAlreadyExistsException.");
        assertEquals(exception.getMessage(), "Cart item already exists for the given product and user.", "Should have the same exception message");
    }

    @Test
    void shouldThrow400CodeStatus_whenAddNewCartItemInvalidUserInDto() {
        // Arrange
        UserDetails mockUserDetails = mock(User.class);

        when(((User) mockUserDetails).getId()).thenReturn(5L);
        when(userService.findByUsername(any())).thenReturn(mockUserDetails);

        // Act
        // Assert
        InvalidMethodArgumentsException exception = assertThrows(InvalidMethodArgumentsException.class,
                () -> cartService.addCartItem(createCartItemDto(), "Invalid user name"),
                "Adding to the cart with invalid user ID dto should throw InvalidMethodArgumentsException.");
        assertEquals(exception.getMessage(), "User id 1 does not match logged in user id", "Should have the same exception message");
    }

    @Test
    void shouldThrow400CodeStatus_whenAddNewCartItemInsufficientProductStock() {
        // Arrange
        UserDetails mockUserDetails = mock(User.class);
        Product mockProduct = productMock();
        mockProduct.setQuantity(1.0);

        when(((User) mockUserDetails).getId()).thenReturn(1L);
        when(userService.findByUsername(any())).thenReturn(mockUserDetails);
        when(productService.findById(1L)).thenReturn(mockProduct);

        // Act
        // Assert
        InsufficientProductStockException exception = assertThrows(InsufficientProductStockException.class,
                () -> cartService.addCartItem(createCartItemDto(), "Invalid user name"),
                "Adding to the cart a cart item with insufficient product stock should throw InsufficientProductStockException.");
        assertEquals(exception.getMessage(), "Product stock is not sufficient", "Should have the same exception message");
    }
}
