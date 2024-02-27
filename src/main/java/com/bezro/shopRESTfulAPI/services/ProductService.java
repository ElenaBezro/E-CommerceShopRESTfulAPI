package com.bezro.shopRESTfulAPI.services;

import com.bezro.shopRESTfulAPI.dtos.CreateProductDto;
import com.bezro.shopRESTfulAPI.entities.Product;

import java.util.Map;

public interface ProductService {
    Product addProduct(CreateProductDto productDto);

    Product findById(Long id);

    Product updateProduct(Long id, CreateProductDto productDto);

    void deleteProduct(Long id);

    Map<String, Object> getProductsPagination(int pageNumber, int pageSize, String sort);

    void decreaseProductStock(Long productId, double decrementAmount);
}