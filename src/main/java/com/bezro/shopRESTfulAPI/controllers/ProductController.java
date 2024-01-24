package com.bezro.shopRESTfulAPI.controllers;

import com.bezro.shopRESTfulAPI.constants.ResponseMessages;
import com.bezro.shopRESTfulAPI.dtos.CreateProductDto;
import com.bezro.shopRESTfulAPI.entities.Product;
import com.bezro.shopRESTfulAPI.exceptions.ApiException;
import com.bezro.shopRESTfulAPI.exceptions.ApiRequestException;
import com.bezro.shopRESTfulAPI.services.ProductService;
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
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/products")
@Tag(description = "Endpoints for selecting, adding, updating and deleting products", name = "Product")
public class ProductController {
    private final ProductService productService;
    private final AuthenticationManager authenticationManager;

    @PostMapping
    @Parameter(in = ParameterIn.HEADER,
            description = "Authorization token",
            name = "JWT",
            content = @Content(schema = @Schema(type = "string")))
    @Operation(summary = "Add a product", description = "Admin can add a new product", tags = {"AddProduct"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = @Content(schema = @Schema(implementation = Product.class))),
            @ApiResponse(responseCode = "400", description = ResponseMessages.ADD_PRODUCT_BAD_REQUEST_MESSAGE,
                    content = @Content(schema = @Schema(implementation = ApiRequestException.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden. Only administrators can access this page",
                    content = @Content(schema = @Schema(implementation = ApiException.class))),
            @ApiResponse(responseCode = "401", description = "User should be authenticated",
                    content = @Content(schema = @Schema(implementation = ApiException.class)))
    })
    public Product addProduct(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Product details",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CreateProductDto.class),
                            examples = @ExampleObject(
                                    value = "{\"name\":\"Example Name\", \"description\":\"Example Description\", \"price\":5.5, \"quantity\":1.5}"
                            )
                    )
            )
            @Valid @RequestBody CreateProductDto productDto) {
        return productService.addProduct(productDto);
    }

    @GetMapping(params = {"pageNumber", "pageSize"})
    @Operation(summary = "Get all products", description = "Any unauthorized user can get a list of all products", tags = {"GetAllProducts"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/CustomResponse")))
    })
    public Map<String, Object> getProducts(
            @Parameter(description = "Page number", example = "1", required = true) @RequestParam(defaultValue = "0") int pageNumber,
            @Parameter(description = "Page size", example = "10", required = true) @RequestParam(defaultValue = "5") int pageSize) {

        return productService.getProductsPagination(pageNumber, pageSize, null);
    }

    @GetMapping(params = {"pageNumber", "pageSize", "sort"})
    @Operation(summary = "Get all products with sorting", description = "Any unauthorized user can get a list of all products", tags = {"GetAllProductsSorted"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = @Content(schema = @Schema(ref = "#/components/schemas/CustomResponse")))
    })
    public Map<String, Object> getProducts(
            @Parameter(description = "Page number", example = "1", required = true) @RequestParam(defaultValue = "0") int pageNumber,
            @Parameter(description = "Page size", example = "10", required = true) @RequestParam(defaultValue = "5") int pageSize,
            @Parameter(description = "Sort order", example = "name", required = true) @RequestParam(defaultValue = "name") String sort) {

        return productService.getProductsPagination(pageNumber, pageSize, sort);
    }

    @PutMapping("/{id}")
    @Parameter(in = ParameterIn.HEADER,
            description = "Authorization token",
            name = "JWT",
            content = @Content(schema = @Schema(type = "string")))
    @Operation(summary = "Update a product", description = "Admin can update a product", tags = {"UpdateProduct"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = @Content(schema = @Schema(implementation = Product.class))),
            @ApiResponse(responseCode = "400", description = ResponseMessages.UPDATE_PRODUCT_BAD_REQUEST_MESSAGE,
                    content = @Content(schema = @Schema(implementation = ApiRequestException.class))),
            @ApiResponse(responseCode = "400", description = "Invalid id",
                    content = @Content(schema = @Schema(implementation = ApiRequestException.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden. Only administrators can access this page",
                    content = @Content(schema = @Schema(implementation = ApiException.class))),
            @ApiResponse(responseCode = "401", description = "User should be authenticated",
                    content = @Content(schema = @Schema(implementation = ApiException.class)))
    })
    public void updateProduct(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Product details",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CreateProductDto.class),
                            examples = @ExampleObject(
                                    value = "{\"name\":\"Example Name\", \"description\":\"Example Description\", \"price\":5.5, \"quantity\":1.5}"
                            )
                    )
            )
            @Valid @RequestBody CreateProductDto productDto, @PathVariable Long id) {
        productService.updateProduct(id, productDto);
    }

    @DeleteMapping("/{id}")
    @Parameter(in = ParameterIn.HEADER,
            description = "Authorization token",
            name = "JWT",
            content = @Content(schema = @Schema(type = "string")))
    @Operation(summary = "Delete a product", description = "Admin can delete a product", tags = {"DeleteProduct"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = @Content(schema = @Schema(implementation = Product.class))),
            @ApiResponse(responseCode = "400", description = "Invalid id",
                    content = @Content(schema = @Schema(implementation = ApiRequestException.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden. Only administrators can access this page",
                    content = @Content(schema = @Schema(implementation = ApiException.class))),
            @ApiResponse(responseCode = "401", description = "User should be authenticated",
                    content = @Content(schema = @Schema(implementation = ApiException.class)))
    })
    public void deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
    }
}
