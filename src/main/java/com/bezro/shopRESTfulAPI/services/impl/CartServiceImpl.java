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
import com.bezro.shopRESTfulAPI.services.CartService;
import com.bezro.shopRESTfulAPI.services.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {
    private final CartRepository cartRepository;
    private final ProductService productService;
    private final UserService userService;

    public CartItemResponse addCartItem(CreateCartItemDto cartItemDto, String username) {
        //TODO: extract userId from principal
        log.info("Adding cart item for user: {}", username);
        Long userId = cartItemDto.getUserId();
        User user = (User) userService.findByUsername(username);
        userMatchPrincipalCheck(userId, user);

        Long productId = cartItemDto.getProductId();
        Product product = productService.findById(productId);
        sufficientProductStockCheck(product, cartItemDto);

        Optional<CartItem> existingCartItem = findCartItemByProductIdAndUserId(productId, userId);

        if (existingCartItem.isPresent()) {
            throw new CartItemAlreadyExistsException("Cart item already exists for the given product and user.");
        }

        CartItem cartItem = createCartItemFromDto(cartItemDto, user, product);
        CartItem storedCartItem = cartRepository.save(cartItem);
        return new CartItemResponse(storedCartItem);
    }

    private CartItem createCartItemFromDto(CreateCartItemDto cartItemDto, User user, Product product) {
        CartItem cartItem = new CartItem();
        cartItem.setProduct(product);
        cartItem.setUser(user);
        cartItem.setQuantity(cartItemDto.getQuantity());
        return cartItem;
    }

    private void sufficientProductStockCheck(Product product, CreateCartItemDto cartItemDto) {
        log.info("Checking product stock for product: {}", product.getName());
        boolean isProductStockSufficient = product.getQuantity() >= cartItemDto.getQuantity();
        if (!isProductStockSufficient) {
            throw new InsufficientProductStockException("Product stock is not sufficient");
        }
    }

    private void userMatchPrincipalCheck(Long userId, User user) {
        log.info("Checking if user id matches logged in user id");
        boolean isUserMatchPrincipal = Objects.equals(userId, user.getId());
        if (!isUserMatchPrincipal) {
            throw new InvalidMethodArgumentsException(
                    String.format("User id %d does not match logged in user id", userId));
        }
    }

    public Optional<CartItem> findCartItemByProductIdAndUserId(Long productId, Long userId) {
        return cartRepository.findByProduct_IdAndUser_Id(productId, userId);
    }

    public CartItemResponse updateCartItemQuantity(CreateCartItemDto cartItemDto, String username, Long cartItemId) {
        log.info("Updating cart item quantity for cart item ID: {}", cartItemId);
        Long userId = cartItemDto.getUserId();
        User user = (User) userService.findByUsername(username);
        userMatchPrincipalCheck(userId, user);

        Long productId = cartItemDto.getProductId();
        Product product = productService.findById(productId);
        sufficientProductStockCheck(product, cartItemDto);

        CartItem cartItem = cartRepository.findById(cartItemId).orElseThrow(() ->
                new InvalidMethodArgumentsException(String.format("Cart item with id: %d does not exist", cartItemId)));
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
        log.info("Removing cart item with id: {}", id);
        CartItem cartItem = cartRepository.findById(id).orElseThrow(() ->
                new InvalidMethodArgumentsException(String.format("Cart item with id: %d does not exist", id)));
        cartRepository.delete(cartItem);
    }

    public List<CartItemResponse> getAllCartItems(String username) {
        log.info("Getting all cart items for user: {}", username);
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
