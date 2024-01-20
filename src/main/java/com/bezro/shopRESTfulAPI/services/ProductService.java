package com.bezro.shopRESTfulAPI.services;

import com.bezro.shopRESTfulAPI.dtos.CreateProductDto;
import com.bezro.shopRESTfulAPI.entities.Product;
import org.springframework.data.domain.Page;

public interface ProductService {
    Product addProduct(CreateProductDto productDto);

    Page<Product> getProductsPagination(Integer pageNumber, Integer pageSize, String sort);
}