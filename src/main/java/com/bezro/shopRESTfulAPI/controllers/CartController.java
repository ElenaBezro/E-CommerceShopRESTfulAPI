package com.bezro.shopRESTfulAPI.controllers;

import com.bezro.shopRESTfulAPI.constants.ResponseMessages;
import com.bezro.shopRESTfulAPI.dtos.CreateCartItemDto;
import com.bezro.shopRESTfulAPI.dtos.CreateProductDto;
import com.bezro.shopRESTfulAPI.entities.CartItem;
import com.bezro.shopRESTfulAPI.entities.Product;
import com.bezro.shopRESTfulAPI.exceptions.ApiException;
import com.bezro.shopRESTfulAPI.exceptions.ApiRequestException;
import com.bezro.shopRESTfulAPI.services.CartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/cart")
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
                    content = @Content(schema = @Schema(implementation = Product.class))),
            @ApiResponse(responseCode = "400", description = ResponseMessages.ADD_CART_ITEM_BAD_REQUEST_MESSAGE,
                    content = @Content(schema = @Schema(implementation = ApiRequestException.class))),
            @ApiResponse(responseCode = "401", description = "User should be authenticated",
                    content = @Content(schema = @Schema(implementation = ApiException.class)))
    })
    public CartItem addCartItem(
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
        return cartService.addCartItem(cartItemDto, principal);
    }

    //TODO: Maybe use Patch?
    @PutMapping("/{id}")
    @Parameter(in = ParameterIn.HEADER,
            description = "Authorization token",
            name = "JWT",
            content = @Content(schema = @Schema(type = "string")))
    @Operation(summary = "Update a cart item quantity", description = "Update a cart item quantity", tags = {"UpdateCartItemQuantity"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = @Content(schema = @Schema(implementation = Product.class))),
            @ApiResponse(responseCode = "400", description = ResponseMessages.ADD_CART_ITEM_BAD_REQUEST_MESSAGE,
                    content = @Content(schema = @Schema(implementation = ApiRequestException.class))),
            @ApiResponse(responseCode = "400", description = "Cart item with id: [id] does not exist",
                    content = @Content(schema = @Schema(implementation = ApiException.class))),
            @ApiResponse(responseCode = "400", description = "Product with id: [id] does not exist",
                    content = @Content(schema = @Schema(implementation = ApiException.class))),
            @ApiResponse(responseCode = "400", description = "User with id: [id] does not exist",
                    content = @Content(schema = @Schema(implementation = ApiException.class))),
            @ApiResponse(responseCode = "400", description = "User id [id] does not match logged in user id",
                    content = @Content(schema = @Schema(implementation = ApiException.class))),
            @ApiResponse(responseCode = "400", description = "Product stock is not sufficient",
                    content = @Content(schema = @Schema(implementation = ApiException.class))),
            @ApiResponse(responseCode = "400", description = "Cart item record with such combination of cartItemId, productId and userId does not exist",
                    content = @Content(schema = @Schema(implementation = ApiException.class))),
            @ApiResponse(responseCode = "401", description = "User should be authenticated",
                    content = @Content(schema = @Schema(implementation = ApiException.class)))
    })
    public CartItem updateCartItemQuantity(
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
        return cartService.updateCartItemQuantity(id, cartItemDto, principal);
    }
}
