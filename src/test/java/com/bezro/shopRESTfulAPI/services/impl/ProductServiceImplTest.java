package com.bezro.shopRESTfulAPI.services.impl;

import com.bezro.shopRESTfulAPI.dtos.CreateProductDto;
import com.bezro.shopRESTfulAPI.entities.Product;
import com.bezro.shopRESTfulAPI.exceptions.InvalidRequestParametersException;
import com.bezro.shopRESTfulAPI.exceptions.NoContentException;
import com.bezro.shopRESTfulAPI.repositories.ProductRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {
    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductServiceImpl productService;

    @AfterEach
    void tearDown() {
        reset(productRepository);
    }

    private CreateProductDto createProductDto() {
        CreateProductDto createProductDto = new CreateProductDto();
        createProductDto.setName("ProductName");
        createProductDto.setDescription("ProductDescription");
        createProductDto.setPrice(2.0d);
        createProductDto.setQuantity(10.5d);

        return createProductDto;
    }

    private Product productMock() {
        Product productMock = new Product();
        productMock.setId(1L);
        productMock.setName("ProductName");
        productMock.setDescription("ProductDescription");
        productMock.setPrice(2.0d);
        productMock.setQuantity(10.5d);

        return productMock;
    }

    private Map<String, Object> createResponseMock(List<Product> paginatedProductsMock, int pageNumber, long totalItems, int pageSize) {
        Map<String, Object> responseMock = new HashMap<>();
        responseMock.put("products", paginatedProductsMock);
        responseMock.put("currentPage", pageNumber);
        responseMock.put("totalItems", totalItems);
        responseMock.put("totalPages", (int) Math.ceil((double) totalItems / pageSize));
        return responseMock;
    }

    @Test
    void shouldReturnProduct_whenAddNewProduct() {
        // Arrange
        CreateProductDto createProductDto = createProductDto();
        Product productMock = productMock();

        when(productRepository.save(any())).thenReturn(productMock);

        // Act
        Product product = productService.addProduct(createProductDto);

        // Assert
        assertSame(product, productMock, "Should be the same object reference");
        assertEquals(product.getId(), productMock.getId(), "Ids should match");
        assertEquals(product.getName(), productMock.getName(), "Names should match");
        assertEquals(product.getDescription(), productMock.getDescription(), "Descriptions should match");
        assertEquals(product.getPrice(), productMock.getPrice(), "Prices should match");
        assertEquals(product.getQuantity(), productMock.getQuantity(), "Quantity should match");
    }

    @Test
        //TODO: change updateProduct in Service to return a Product?
    void shouldReturnVoid_whenUpdateProduct() {
        // Arrange
        CreateProductDto createProductDto = createProductDto();
        Product productMock = productMock();

        when(productRepository.findById(eq(1L))).thenReturn(Optional.of(productMock));

        // Act
        productService.updateProduct(1L, createProductDto);

        // Assert
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    void shouldThrowInvalidRequestParameters_whenUpdateProductWithInvalidId() {
        // Arrange
        CreateProductDto createProductDto = new CreateProductDto();
        when(productRepository.findById(eq(1L))).thenReturn(Optional.empty());

        // Act
        // Assert
        InvalidRequestParametersException exception = assertThrows(InvalidRequestParametersException.class,
                () -> productService.updateProduct(1L, createProductDto),
                "Updating non-existing product should throw InvalidRequestParametersException.");
        assertEquals(exception.getMessage(), "Product with id: 1 does not exist", "Should have the same exception message");
    }

    @Test
    void shouldReturnVoid_whenDeleteProduct() {
        // Arrange
        Product productMock = productMock();

        when(productRepository.findById(eq(1L))).thenReturn(Optional.of(productMock));

        // Act
        productService.deleteProduct(1L);

        // Assert
        verify(productRepository, times(1)).delete(any(Product.class));
    }

    @Test
    void shouldThrowInvalidRequestParameters_whenDeleteProductWithInvalidId() {
        // Arrange
        when(productRepository.findById(eq(1L))).thenReturn(Optional.empty());

        // Act
        // Assert
        InvalidRequestParametersException exception = assertThrows(InvalidRequestParametersException.class,
                () -> productService.deleteProduct(1L),
                "Deleting non-existing product should throw InvalidRequestParametersException.");
        assertEquals(exception.getMessage(), "Product with id: 1 does not exist", "Should have the same exception message");
    }

}
