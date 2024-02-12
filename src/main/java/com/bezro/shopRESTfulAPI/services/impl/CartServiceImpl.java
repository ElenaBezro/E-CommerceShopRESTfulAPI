package com.bezro.shopRESTfulAPI.services.impl;

import com.bezro.shopRESTfulAPI.dtos.CartItemResponse;
import com.bezro.shopRESTfulAPI.dtos.CreateCartItemDto;
import com.bezro.shopRESTfulAPI.entities.CartItem;
import com.bezro.shopRESTfulAPI.entities.Product;
import com.bezro.shopRESTfulAPI.entities.User;
import com.bezro.shopRESTfulAPI.exceptions.InvalidMethodArgumentsException;
import com.bezro.shopRESTfulAPI.repositories.CartRepository;
import com.bezro.shopRESTfulAPI.services.CartService;
import com.bezro.shopRESTfulAPI.services.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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

    public CartItemResponse addOrUpdateCartItem(CreateCartItemDto cartItemDto, String username, Long cartItemId) {
        //TODO: extract userId from principal
        Long userId = cartItemDto.getUserId();
        userMatchPrincipalCheck(userId, username);

        Long productId = cartItemDto.getProductId();
        Product product = productService.findById(productId);
        sufficientProductStockCheck(product, cartItemDto);

        Optional<CartItem> existingCartItem = findCartItemByProductIdAndUserId(productId, userId);

        if (cartItemId != null && existingCartItem.isEmpty()) {
            throw new InvalidMethodArgumentsException(
                    "Cart item record with such combination of cartItemId, productId and userId does not exist");
        }

        if (existingCartItem.isPresent()) {
            if (cartItemId != null && !existingCartItem.get().getId().equals(cartItemId)) {
                throw new InvalidMethodArgumentsException(String.format("Cart item with id: %d does not exist", cartItemId));
            }

            return updateCartItemQuantity(existingCartItem.get().getId(), cartItemDto);
        } else {
            return addCartItem(cartItemDto, userId, product);
        }
    }

    private CartItemResponse addCartItem(CreateCartItemDto cartItemDto, Long userId, Product product) {
        CartItem cartItem = createCartItemFromDto(cartItemDto, userId, product);
        CartItem storedCartItem = cartRepository.save(cartItem);
        return new CartItemResponse(storedCartItem);
    }

    private CartItem createCartItemFromDto(CreateCartItemDto cartItemDto, Long userId, Product product) {
        CartItem cartItem = new CartItem();
        cartItem.setProduct(product);
        cartItem.setUser(userService.findById(userId));
        cartItem.setQuantity(cartItemDto.getQuantity());
        return cartItem;
    }

    private void sufficientProductStockCheck(Product product, CreateCartItemDto cartItemDto) {
        boolean isProductStockSufficient = product.getQuantity() >= cartItemDto.getQuantity();
        if (!isProductStockSufficient) {
            throw new InvalidMethodArgumentsException("Product stock is not sufficient");
        }
    }

    private void userMatchPrincipalCheck(Long userId, String username) {
        boolean isUserMatchPrincipal = userService.isUserMatchPrincipal(userId, username);
        if (!isUserMatchPrincipal) {
            throw new InvalidMethodArgumentsException(
                    String.format("User id %d does not match logged in user id", userId));
        }
    }

    public Optional<CartItem> findCartItemByProductIdAndUserId(Long productId, Long userId) {
        return cartRepository.findByProduct_IdAndUser_Id(productId, userId);
    }

    public CartItemResponse updateCartItemQuantity(Long id, CreateCartItemDto cartItemDto) {
        CartItem cartItem = cartRepository.findById(id).orElseThrow(() ->
                new InvalidMethodArgumentsException(String.format("Cart item with id: %d does not exist", id)));
        cartItemValidRecordCheck(cartItem, cartItemDto);
        cartItem.setQuantity(cartItemDto.getQuantity());

        CartItem storedCartItem = cartRepository.save(cartItem);
        return new CartItemResponse(storedCartItem);
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

    public List<CartItemResponse> getAllCartItems(String username) {
        User user = (User) userService.findByUsername(username);
        Long userId = user.getId();
        List<CartItem> cartItemList = getAllCartItems(userId);
        return cartItemList.stream()
                .map(CartItemResponse::new).toList();
    }

    public List<CartItem> getAllCartItems(Long userId) {
        Optional<List<CartItem>> cartItemList = cartRepository.findByUser_Id(userId);
        return cartItemList.orElseGet(ArrayList::new);
    }
}
