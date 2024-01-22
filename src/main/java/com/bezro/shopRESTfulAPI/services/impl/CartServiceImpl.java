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
        CartItem cartItem;

        if (existsByProductId(cartItemDto.getProductId())) {
            //TODO: create method getCartItemByProduct_Id
            cartItem = cartRepository.findByProduct_Id(cartItemDto.getProductId()).get();
            updateCartItemQuantity(cartItem.getId(), cartItemDto, principal);
        } else {

            cartItem = new CartItem();
            cartItem.setProduct(productService.findById(cartItemDto.getProductId()));
            Long userId = cartItemDto.getUserId();
            //TODO: throw this exception in userService
            cartItem.setUser(userService.findById(userId)
                    .orElseThrow(() -> new InvalidMethodArgumentsException(
                            String.format("User with id: %d does not exist", userId))));
            boolean isUserMatchPrincipal = userService.isUserMatchPrincipal(userId, principal);
            if (!isUserMatchPrincipal) {
                throw new InvalidMethodArgumentsException(
                        String.format("User id %d does not match logged in user id", userId));
            }
            cartItem.setQuantity(cartItemDto.getQuantity());
            return cartRepository.save(cartItem);
        }
        return null;
    }


    public boolean existsByProductId(Long productId) {
        return cartRepository.findByProduct_Id(productId).isPresent();
    }

    public void updateCartItemQuantity(Long id, CreateCartItemDto cartItemDto, Principal principal) {
        CartItem cartItem = cartRepository.findById(id).orElseThrow(() ->
                new InvalidMethodArgumentsException(String.format("Cart item with id: %d does not exist", id)));
        //TODO: use ModelMapper to map data from DTO to the entity
        //    modelMapper.map(cartItemDto, cartItem) ?
        cartItem.setProduct(productService.findById(cartItemDto.getProductId()));
        Long userId = cartItemDto.getUserId();
        //TODO: throw this exception in userService
        cartItem.setUser(userService.findById(userId)
                .orElseThrow(() -> new InvalidMethodArgumentsException(
                        String.format("User with id: %d does not exist", userId))));
        boolean isUserMatchPrincipal = userService.isUserMatchPrincipal(userId, principal);
        if (!isUserMatchPrincipal) {
            throw new InvalidMethodArgumentsException(
                    String.format("User id %d does not match logged in user id", userId));
        }
        cartItem.setQuantity(cartItemDto.getQuantity());
        cartRepository.save(cartItem);
    }

}
