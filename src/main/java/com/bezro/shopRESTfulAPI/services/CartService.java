package com.bezro.shopRESTfulAPI.services;

import com.bezro.shopRESTfulAPI.dtos.CartItemResponse;
import com.bezro.shopRESTfulAPI.dtos.CreateCartItemDto;
import com.bezro.shopRESTfulAPI.entities.CartItem;

import java.util.List;

public interface CartService {
    CartItemResponse addCartItem(CreateCartItemDto cartItemDto, String username);

    CartItemResponse updateCartItemQuantity(CreateCartItemDto cartItemDto, String username, Long cartItemId);

    void removeCartItem(Long id);

    List<CartItemResponse> getAllCartItems(String username);

    List<CartItem> getAllCartItems(Long userId);
}