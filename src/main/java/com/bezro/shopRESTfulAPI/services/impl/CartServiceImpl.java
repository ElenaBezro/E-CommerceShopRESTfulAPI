package com.bezro.shopRESTfulAPI.services.impl;

import com.bezro.shopRESTfulAPI.dtos.CreateCartItemDto;
import com.bezro.shopRESTfulAPI.entities.CartItem;
import com.bezro.shopRESTfulAPI.entities.Product;
import com.bezro.shopRESTfulAPI.entities.User;
import com.bezro.shopRESTfulAPI.exceptions.InvalidMethodArgumentsException;
import com.bezro.shopRESTfulAPI.repositories.CartRepository;
import com.bezro.shopRESTfulAPI.services.CartService;
import com.bezro.shopRESTfulAPI.services.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {
    private final CartRepository cartRepository;
    private final ProductService productService;
    private final UserService userService;

    public CartItem addCartItem(CreateCartItemDto cartItemDto, Principal principal) {
        Long productId = cartItemDto.getProductId();
        Product product = productService.findById(productId);
        sufficientProductStockCheck(product, cartItemDto);

        CartItem cartItem;
        Long userId = cartItemDto.getUserId();

        if (existsByProductId(productId)) {
            cartItem = findCartItemByProductId(productId);
            return updateCartItemQuantity(cartItem.getId(), cartItemDto, principal);
        } else {
            cartItem = new CartItem();
            cartItem.setProduct(product);
            cartItem.setUser(userService.findById(userId));
            userMatchPrincipalCheck(userId, principal);
            cartItem.setQuantity(cartItemDto.getQuantity());

            return cartRepository.save(cartItem);
        }
    }

    private void sufficientProductStockCheck(Product product, CreateCartItemDto cartItemDto) {
        boolean isProductStockSufficient = product.getQuantity() >= cartItemDto.getQuantity();
        if (!isProductStockSufficient) {
            throw new InvalidMethodArgumentsException("Product stock is not sufficient");
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
        Long userId = cartItemDto.getUserId();
        Long productId = cartItemDto.getProductId();
        Product product = productService.findById(productId);
        sufficientProductStockCheck(product, cartItemDto);

        CartItem cartItem = cartRepository.findById(id).orElseThrow(() ->
                new InvalidMethodArgumentsException(String.format("Cart item with id: %d does not exist", id)));
        cartItemValidRecordCheck(cartItem, cartItemDto);
        userMatchPrincipalCheck(userId, principal);
        cartItem.setQuantity(cartItemDto.getQuantity());

        return cartRepository.save(cartItem);
    }

    private void cartItemValidRecordCheck(CartItem cartItem, CreateCartItemDto cartItemDto) {
        boolean isSameProduct = Objects.equals(cartItem.getProduct().getId(), cartItemDto.getProductId());
        boolean isSameUser = Objects.equals(cartItem.getUser().getId(), cartItemDto.getUserId());
        if (!(isSameProduct && isSameUser)) {
            throw new InvalidMethodArgumentsException(
                    "Cart item record with such combination of cartItemId, productId and userId does not exist");
        }
    }

    public void removeCartItem(Long id) {
        CartItem cartItem = cartRepository.findById(id).orElseThrow(() ->
                new InvalidMethodArgumentsException(String.format("Cart item with id: %d does not exist", id)));
        cartRepository.delete(cartItem);
    }

    public List<CartItem> getAllCartItems(Principal principal) {
        User user = (User) userService.findByUsername(principal.getName());
        Long userId = user.getId();
        Optional<List<CartItem>> cartItemList = cartRepository.findByUser_Id(userId);
        return cartItemList.orElseGet(ArrayList::new);
    }

    //TODO: refactor two last methods: remove repeated code
    public List<CartItem> getAllCartItems(Long userId) {
        Optional<List<CartItem>> cartItemList = cartRepository.findByUser_Id(userId);
        return cartItemList.orElseGet(ArrayList::new);
    }
}
