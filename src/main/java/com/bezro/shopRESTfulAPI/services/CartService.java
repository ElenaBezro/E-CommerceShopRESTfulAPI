package com.bezro.shopRESTfulAPI.services;

import com.bezro.shopRESTfulAPI.dtos.CartItemResponse;
import com.bezro.shopRESTfulAPI.dtos.CreateCartItemDto;
import com.bezro.shopRESTfulAPI.entities.CartItem;

import java.util.List;

public interface CartService {
    CartItemResponse addOrUpdateCartItem(CreateCartItemDto cartItemDto, Long userId, Long cartItemId);

    CartItemResponse updateCartItemQuantity(Long id, CreateCartItemDto cartItemDto);

    void removeCartItem(Long id);

    List<CartItemResponse> getAllCartItems(Long userId);

    List<CartItem> getAllCartItemsByUserId(Long userId);
}