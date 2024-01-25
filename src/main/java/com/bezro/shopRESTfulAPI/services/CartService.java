package com.bezro.shopRESTfulAPI.services;

import com.bezro.shopRESTfulAPI.dtos.CreateCartItemDto;
import com.bezro.shopRESTfulAPI.entities.CartItem;

import java.security.Principal;
import java.util.List;

public interface CartService {
    CartItem addCartItem(CreateCartItemDto cartItemDto, Principal principal);

    CartItem updateCartItemQuantity(Long id, CreateCartItemDto cartItemDto, Principal principal);

    void removeCartItem(Long id);

    List<CartItem> getAllCartItems(Principal principal);

    List<CartItem> getAllCartItems(Long userId);
}