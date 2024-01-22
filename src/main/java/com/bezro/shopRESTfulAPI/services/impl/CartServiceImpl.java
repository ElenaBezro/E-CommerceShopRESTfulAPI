package com.bezro.shopRESTfulAPI.services.impl;

import com.bezro.shopRESTfulAPI.dtos.CreateCartItemDto;
import com.bezro.shopRESTfulAPI.entities.CartItem;
import com.bezro.shopRESTfulAPI.exceptions.InvalidMethodArgumentsException;
import com.bezro.shopRESTfulAPI.repositories.CartRepository;
import com.bezro.shopRESTfulAPI.services.CartService;
import com.bezro.shopRESTfulAPI.services.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.Principal;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {
    private final CartRepository cartRepository;
    private final ProductService productService;
    private final UserService userService;


    public CartItem addCartItem(CreateCartItemDto cartItemDto, Principal principal) {
        //TODO: check if product Quantity >= cart Item Quantity
        CartItem cartItem;
        Long productId = cartItemDto.getProductId();
        Long userId = cartItemDto.getUserId();

        if (existsByProductId(productId)) {
            cartItem = findCartItemByProductId(productId);
            return updateCartItemQuantity(cartItem.getId(), cartItemDto, principal);
        } else {
            cartItem = new CartItem();
            cartItem.setProduct(productService.findById(productId));
            cartItem.setUser(userService.findByIdOrThrow(userId));
            userMatchPrincipalCheck(userId, principal);
            cartItem.setQuantity(cartItemDto.getQuantity());

            return cartRepository.save(cartItem);
        }
    }

    private void userMatchPrincipalCheck(Long userId, Principal principal) {
        boolean isUserMatchPrincipal = userService.isUserMatchPrincipal(userId, principal);
        if (!isUserMatchPrincipal) {
            throw new InvalidMethodArgumentsException(
                    String.format("User id %d does not match logged in user id", userId));
        }
    }

    public boolean existsByProductId(Long productId) {
        return cartRepository.findByProduct_Id(productId).isPresent();
    }

    public CartItem findCartItemByProductId(Long productId) {
        return cartRepository.findByProduct_Id(productId)
                .orElseThrow(() -> new InvalidMethodArgumentsException(String.format("Cart item with product id: %d does not exist", productId)));
    }

    public CartItem updateCartItemQuantity(Long id, CreateCartItemDto cartItemDto, Principal principal) {
        //TODO: check if product Quantity >= cart Item Quantity
        Long userId = cartItemDto.getUserId();
        Long productId = cartItemDto.getProductId();

        CartItem cartItem = cartRepository.findById(id).orElseThrow(() ->
                new InvalidMethodArgumentsException(String.format("Cart item with id: %d does not exist", id)));
        //TODO: use ModelMapper to map data from DTO to the entity
        //    modelMapper.map(cartItemDto, cartItem) ?
        cartItem.setProduct(productService.findById(productId));
        cartItem.setUser(userService.findByIdOrThrow(userId));
        userMatchPrincipalCheck(userId, principal);
        cartItem.setQuantity(cartItemDto.getQuantity());

        return cartRepository.save(cartItem);
    }

}
