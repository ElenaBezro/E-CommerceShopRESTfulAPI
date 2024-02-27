package com.bezro.shopRESTfulAPI.controllers;

import com.bezro.shopRESTfulAPI.constants.ResponseMessages;
import com.bezro.shopRESTfulAPI.dtos.CartItemResponse;
import com.bezro.shopRESTfulAPI.dtos.CreateCartItemDto;
import com.bezro.shopRESTfulAPI.dtos.CreateProductDto;
import com.bezro.shopRESTfulAPI.exceptions.ApiException;
import com.bezro.shopRESTfulAPI.exceptions.ExceptionResponse;
import com.bezro.shopRESTfulAPI.services.CartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/cart-items")
@Tag(description = "Endpoints for selecting, adding, updating and deleting cart items", name = "Cart")
public class CartController {
    private final CartService cartService;

    @PostMapping
    @Parameter(in = ParameterIn.HEADER,
            description = "Authorization token",
            name = "JWT",
            content = @Content(schema = @Schema(type = "string")))
    @Operation(summary = "Add a cart item", description = "Add a cart item", tags = {"AddCartItem"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = @Content(schema = @Schema(implementation = CartItemResponse.class))),
            @ApiResponse(responseCode = "400", description = ResponseMessages.ADD_CART_ITEM_BAD_REQUEST_MESSAGE,
                    content = @Content(schema = @Schema(implementation = ExceptionResponse.class))),
            @ApiResponse(responseCode = "400", description = "The user id does not match logged-in user's id",
                    content = @Content(schema = @Schema(implementation = ApiException.class))),
            @ApiResponse(responseCode = "400", description = "Invalid product id",
                    content = @Content(schema = @Schema(implementation = ApiException.class))),
            @ApiResponse(responseCode = "400", description = "Insufficient product stock",
                    content = @Content(schema = @Schema(implementation = ApiException.class))),
            @ApiResponse(responseCode = "400", description = "Cart item already exists for the given product and user.",
                    content = @Content(schema = @Schema(implementation = ApiException.class))),
            @ApiResponse(responseCode = "401", description = "User should be authenticated",
                    content = @Content(schema = @Schema(implementation = ApiException.class)))
    })
    public CartItemResponse addCartItem(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Cart item details",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CreateProductDto.class),
                            examples = @ExampleObject(
                                    value = "{\"productId\": 100, \"userId\": 10, \"quantity\":1.5}"
                            )
                    )
            )
            @Valid @RequestBody CreateCartItemDto cartItemDto, Principal principal) {
        log.info("Adding cart item: {}", cartItemDto);
        return cartService.addCartItem(cartItemDto, principal.getName());
    }

    @PutMapping("/{id}")
    @Parameter(in = ParameterIn.HEADER,
            description = "Authorization token",
            name = "JWT",
            content = @Content(schema = @Schema(type = "string")))
    @Operation(summary = "Update a cart item quantity", description = "Update a cart item quantity", tags = {"UpdateCartItemQuantity"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = @Content(schema = @Schema(implementation = CartItemResponse.class))),
            @ApiResponse(responseCode = "400", description = ResponseMessages.ADD_CART_ITEM_BAD_REQUEST_MESSAGE,
                    content = @Content(schema = @Schema(implementation = ExceptionResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid cart item id",
                    content = @Content(schema = @Schema(implementation = ApiException.class))),
            @ApiResponse(responseCode = "400", description = "Invalid product id",
                    content = @Content(schema = @Schema(implementation = ApiException.class))),
            @ApiResponse(responseCode = "400", description = "The user id does not match logged-in user's id",
                    content = @Content(schema = @Schema(implementation = ApiException.class))),
            @ApiResponse(responseCode = "400", description = "Insufficient product stock",
                    content = @Content(schema = @Schema(implementation = ApiException.class))),
            @ApiResponse(responseCode = "400", description = "invalid combination of payload fields",
                    content = @Content(schema = @Schema(implementation = ApiException.class))),
            @ApiResponse(responseCode = "401", description = "User should be authenticated",
                    content = @Content(schema = @Schema(implementation = ApiException.class)))
    })
    public CartItemResponse updateCartItemQuantity(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Cart item details",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CreateCartItemDto.class),
                            examples = @ExampleObject(
                                    value = "{\"productId\": 100, \"userId\": 10, \"quantity\":1.5}"
                            )
                    )
            )
            @Valid @RequestBody CreateCartItemDto cartItemDto, @PathVariable Long id, Principal principal) {
        return cartService.updateCartItemQuantity(cartItemDto, principal.getName(), id);
    }

    @DeleteMapping("/{id}")
    @Parameter(in = ParameterIn.HEADER,
            description = "Authorization token",
            name = "JWT",
            content = @Content(schema = @Schema(type = "string")))
    @Operation(summary = "Remove a cart item", description = "Remove a cart item", tags = {"RemoveCartItem"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation"),
            @ApiResponse(responseCode = "400", description = "Invalid cart item id",
                    content = @Content(schema = @Schema(implementation = ExceptionResponse.class))),
            @ApiResponse(responseCode = "401", description = "User should be authenticated",
                    content = @Content(schema = @Schema(implementation = ApiException.class)))
    })
    public void removeCartItem(@PathVariable Long id) {
        log.info("Removing cart item with id: {}", id);
        cartService.removeCartItem(id);
    }

    @GetMapping
    @Operation(summary = "Get all cart items", description = "Get a list of all cart items", tags = {"GetAllCartItems"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = CartItemResponse.class)))),
            @ApiResponse(responseCode = "401", description = "User should be authenticated",
                    content = @Content(schema = @Schema(implementation = ApiException.class)))
    })
    public List<CartItemResponse> getAllCartItems(Principal principal) {
        log.info("Getting all cart items for user: {}", principal.getName());
        return cartService.getAllCartItems(principal.getName());
    }
}
