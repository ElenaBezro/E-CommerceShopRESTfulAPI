package com.bezro.shopRESTfulAPI.services.impl;

import com.bezro.shopRESTfulAPI.dtos.CreateProductDto;
import com.bezro.shopRESTfulAPI.entities.Product;
import com.bezro.shopRESTfulAPI.exceptions.InvalidMethodArgumentsException;
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
        createProductDto.setName("New ProductName");
        createProductDto.setDescription("New ProductDescription");
        createProductDto.setPrice(4.0d);
        createProductDto.setQuantity(20.5d);

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
    void shouldReturnVoid_whenUpdateProduct() {
        // Arrange
        CreateProductDto createProductDto = createProductDto();
        Product productMock = productMock();

        Product expectedProduct = new Product();
        expectedProduct.setId(productMock.getId());
        expectedProduct.setName(createProductDto.getName());
        expectedProduct.setDescription(createProductDto.getDescription());
        expectedProduct.setPrice(createProductDto.getPrice());
        expectedProduct.setQuantity(createProductDto.getQuantity());

        when(productRepository.findById(eq(1L))).thenReturn(Optional.of(productMock));
        when(productRepository.save(any())).thenReturn(expectedProduct);

        // Act
        Product product = productService.updateProduct(1L, createProductDto);

        // Assert
        verify(productRepository, times(1)).save(any(Product.class));
        assertEquals(expectedProduct.getId(), product.getId(), "Ids should match");
        assertEquals(expectedProduct.getName(), product.getName(), "Names should match");
        assertEquals(expectedProduct.getDescription(), product.getDescription(), "Descriptions should match");
        assertEquals(expectedProduct.getPrice(), product.getPrice(), "Prices should match");
        assertEquals(expectedProduct.getQuantity(), product.getQuantity(), "Quantity should match");
    }

    @Test
    void shouldThrowInvalidRequestParameters_whenUpdateProductWithInvalidId() {
        // Arrange
        CreateProductDto createProductDto = new CreateProductDto();
        when(productRepository.findById(eq(1L))).thenReturn(Optional.empty());

        // Act
        // Assert
        InvalidMethodArgumentsException exception = assertThrows(InvalidMethodArgumentsException.class,
                () -> productService.updateProduct(1L, createProductDto),
                "Updating non-existing product should throw InvalidMethodArgumentsException.");
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
        InvalidMethodArgumentsException exception = assertThrows(InvalidMethodArgumentsException.class,
                () -> productService.deleteProduct(1L),
                "Deleting non-existing product should throw InvalidMethodArgumentsException.");
        assertEquals(exception.getMessage(), "Product with id: 1 does not exist", "Should have the same exception message");
    }

    @Test
    void shouldReturnObjectWithProducts_whenGetProductsPaginationWithoutSort() {
        //Arrange
        List<Product> paginatedProductsMock = new ArrayList<>();
        paginatedProductsMock.add(productMock());

        int pageNumber = 0;
        int pageSize = 2;
        long totalItems = paginatedProductsMock.size();

        Map<String, Object> responseMock = createResponseMock(paginatedProductsMock, pageNumber, totalItems, pageSize);
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<Product> productPage = new PageImpl<>(paginatedProductsMock, pageable, totalItems);

        when(productRepository.findAll(eq(pageable))).thenReturn(productPage);

        //Act
        Map<String, Object> response = productService.getProductsPagination(pageNumber, pageSize, null);
        List<Product> productsFromResponse = (List<Product>) response.get("products");

        //Assert
        assertEquals(response.size(), responseMock.size(), "Should have the same size");
        assertTrue(response.containsKey("products"), "Should contain 'products' key");
        assertTrue(response.containsKey("currentPage"), "Should contain 'currentPage' key");
        assertTrue(response.containsKey("totalItems"), "Should contain 'totalItems' key");
        assertTrue(response.containsKey("totalPages"), "Should contain 'totalPages' key");
        assertEquals(response.get("products").toString(), responseMock.get("products").toString(), "Products list should match");
        assertEquals(response.get("currentPage"), responseMock.get("currentPage"), "Current Page should match");
        assertEquals(response.get("totalItems"), responseMock.get("totalItems"), "Total Items should match");
        assertEquals(response.get("totalPages"), responseMock.get("totalPages"), "Total Pages should match");
        assertEquals(paginatedProductsMock.get(0).getName(), productsFromResponse.get(0).getName(), "The first item should have 'ProductName'");
    }

    @Test
    void shouldReturnObjectWithSortedProducts_whenGetProductsPaginationWithSort() {
        //Arrange
        List<Product> paginatedProductsMock = new ArrayList<>();
        paginatedProductsMock.add(productMock());

        int pageNumber = 0;
        int pageSize = 2;
        long totalItems = paginatedProductsMock.size();
        String sort = "name";

        Map<String, Object> responseMock = createResponseMock(paginatedProductsMock, pageNumber, totalItems, pageSize);
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.Direction.ASC, sort);
        Page<Product> productPage = new PageImpl<>(paginatedProductsMock, pageable, totalItems);

        when(productRepository.findAll(eq(pageable))).thenReturn(productPage);

        //Act
        Map<String, Object> response = productService.getProductsPagination(pageNumber, pageSize, sort);
        List<Product> productsFromResponse = (List<Product>) response.get("products");

        //Assert
        assertEquals(responseMock.size(), response.size(), "Should have the same size");
        assertTrue(response.containsKey("products"), "Should contain 'products' key");
        assertTrue(response.containsKey("currentPage"), "Should contain 'currentPage' key");
        assertTrue(response.containsKey("totalItems"), "Should contain 'totalItems' key");
        assertTrue(response.containsKey("totalPages"), "Should contain 'totalPages' key");
        assertEquals(responseMock.get("products").toString(), response.get("products").toString(), "Products list should match");
        assertEquals(responseMock.get("currentPage"), response.get("currentPage"), "Current Page should match");
        assertEquals(responseMock.get("totalItems"), response.get("totalItems"), "Total Items should match");
        assertEquals(responseMock.get("totalPages"), response.get("totalPages"), "Total Pages should match");
        assertEquals(paginatedProductsMock.get(0).getName(), productsFromResponse.get(0).getName(), "The first item should have 'ProductName'");
    }

    @Test
    void shouldThrowNoContent_whenGetNonExistingProductsPage() {
        // Arrange
        int pageNumberWithNoContent = 10;
        int pageSize = 2;
        Pageable pageable = PageRequest.of(pageNumberWithNoContent, pageSize);
        Page<Product> emptyProductPage = Page.empty();
        when(productRepository.findAll(eq(pageable))).thenReturn(emptyProductPage);

        // Act
        // Assert
        NoContentException exception = assertThrows(NoContentException.class,
                () -> productService.getProductsPagination(pageNumberWithNoContent, pageSize, null),
                "Getting a page with no content should throw NoContentException.");
        assertEquals(exception.getMessage(), "No Content", "Should have the same exception message");
        verify(productRepository, times(1)).findAll(any(Pageable.class));
    }
}
