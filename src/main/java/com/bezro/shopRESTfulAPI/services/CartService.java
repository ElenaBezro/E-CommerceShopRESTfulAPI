package com.bezro.shopRESTfulAPI.services;

import com.bezro.shopRESTfulAPI.dtos.CreateCartItemDto;
import com.bezro.shopRESTfulAPI.entities.CartItem;

import java.security.Principal;

public interface CartService {
    CartItem addCartItem(CreateCartItemDto cartItemDto, Principal principal);

//    void updateCartItem(Long id, CreateCartItemDto cartItemDto);
//
//    void deleteCartItem(Long id);

}