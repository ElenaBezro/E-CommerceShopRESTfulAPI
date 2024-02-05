package com.bezro.shopRESTfulAPI.controllers;

import com.bezro.shopRESTfulAPI.constants.ResponseMessages;
import com.bezro.shopRESTfulAPI.dtos.OrderResponse;
import com.bezro.shopRESTfulAPI.dtos.UpdateOrderDto;
import com.bezro.shopRESTfulAPI.exceptions.ApiException;
import com.bezro.shopRESTfulAPI.exceptions.ExceptionResponse;
import com.bezro.shopRESTfulAPI.services.OrderService;
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
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/orders")
@Tag(description = "Endpoints for creating, getting, updating an order and getting all user orders", name = "Orders")
public class OrderController {
    private final OrderService orderService;

    @PostMapping
    @Parameter(in = ParameterIn.HEADER,
            description = "Authorization token",
            name = "JWT",
            content = @Content(schema = @Schema(type = "string")))
    @Operation(summary = "Create an order", description = "Create an order", tags = {"CreateOrder"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = @Content(schema = @Schema(implementation = OrderResponse.class))),
            @ApiResponse(responseCode = "400", description = "Cannot create order with an empty cart",
                    content = @Content(schema = @Schema(implementation = ApiException.class))),
            @ApiResponse(responseCode = "400", description = "Not enough product stock",
                    content = @Content(schema = @Schema(implementation = ExceptionResponse.class))),
            @ApiResponse(responseCode = "401", description = "User should be authenticated",
                    content = @Content(schema = @Schema(implementation = ApiException.class)))
    })
    public OrderResponse createOrder(Principal principal) {

        return orderService.createOrder(principal.getName());
    }

    @PatchMapping("/{orderId}")
    @Parameter(in = ParameterIn.HEADER,
            description = "Authorization token",
            name = "JWT",
            content = @Content(schema = @Schema(type = "string")))
    @Operation(summary = "Update order status", description = "Update order status", tags = {"Update order status"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = @Content(schema = @Schema(implementation = OrderResponse.class))),
            @ApiResponse(responseCode = "400", description = ResponseMessages.UPDATE_ORDER_BAD_REQUEST_MESSAGE,
                    content = @Content(schema = @Schema(implementation = ExceptionResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid order id",
                    content = @Content(schema = @Schema(implementation = ApiException.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden. Only administrators can access this page",
                    content = @Content(schema = @Schema(implementation = ApiException.class))),
            @ApiResponse(responseCode = "401", description = "User should be authenticated",
                    content = @Content(schema = @Schema(implementation = ApiException.class)))
    })
    public OrderResponse updateOrderStatus(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Order details",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UpdateOrderDto.class),
                            examples = @ExampleObject(
                                    value = "{\"status\":\"SHIPPED\"}"
                            )
                    )
            )
            @Valid @RequestBody UpdateOrderDto updateOrderDto, @PathVariable Long orderId) {
        return orderService.updateOrderStatus(updateOrderDto, orderId);
    }

    @GetMapping
    @Operation(summary = "Get all user orders", description = "Get all user orders", tags = {"GetAllUserOrders"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = OrderResponse.class)))),
            @ApiResponse(responseCode = "401", description = "User should be authenticated",
                    content = @Content(schema = @Schema(implementation = ApiException.class)))
    })
    public List<OrderResponse> getAllOrders(Principal principal) {
        return orderService.getAllOrders(principal.getName());
    }
}
