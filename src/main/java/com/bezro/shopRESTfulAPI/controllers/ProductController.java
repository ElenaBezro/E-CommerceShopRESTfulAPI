package com.bezro.shopRESTfulAPI.controllers;

import com.bezro.shopRESTfulAPI.dtos.CreateProductDto;
import com.bezro.shopRESTfulAPI.entities.Product;
import com.bezro.shopRESTfulAPI.exceptions.ApiException;
import com.bezro.shopRESTfulAPI.services.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/products")
@Tag(description = "Endpoints for selecting, adding, updating and deleting products", name = "Product")
public class ProductController {
    private final ProductService productService;
    private final AuthenticationManager authenticationManager;

    //TODO: How to access "/" POST for admin and "/" GET for users?
    @PostMapping
    @Operation(summary = "Add a product", description = "Admin can add a new product", tags = {"Product"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = @Content(schema = @Schema(implementation = Product.class))),
            @ApiResponse(responseCode = "401", description = "Sorry, only administrators can access this page.",
                    content = @Content(schema = @Schema(implementation = ApiException.class)))
    })
    public Product addProduct(@Valid @RequestBody CreateProductDto productDto) {
        return productService.addProduct(productDto);
    }

}
