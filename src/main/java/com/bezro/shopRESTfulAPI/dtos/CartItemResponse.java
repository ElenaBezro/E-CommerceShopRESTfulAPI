package com.bezro.shopRESTfulAPI.dtos;

import com.bezro.shopRESTfulAPI.entities.CartItem;
import com.bezro.shopRESTfulAPI.entities.Product;
import lombok.Data;

@Data
public class CartItemResponse {
    private Long id;

    private Product product;

    private Long userId;

    private double quantity;

    public CartItemResponse(CartItem cartItem) {
        this.id = cartItem.getId();
        this.product = cartItem.getProduct();
        this.userId = cartItem.getUser().getId();
        this.quantity = cartItem.getQuantity();
    }
}