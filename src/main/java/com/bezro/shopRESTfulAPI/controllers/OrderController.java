package com.bezro.shopRESTfulAPI.controllers;

import com.bezro.shopRESTfulAPI.entities.Order;
import com.bezro.shopRESTfulAPI.exceptions.ApiException;
import com.bezro.shopRESTfulAPI.services.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

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
                    content = @Content(schema = @Schema(implementation = Order.class))),
            @ApiResponse(responseCode = "401", description = "User should be authenticated",
                    content = @Content(schema = @Schema(implementation = ApiException.class)))
    })
    public Order createOrder(Principal principal) {
        return orderService.createOrder(principal);
    }

}