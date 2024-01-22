package com.bezro.shopRESTfulAPI.services;

import com.bezro.shopRESTfulAPI.dtos.CreateProductDto;
import com.bezro.shopRESTfulAPI.entities.Product;
import org.springframework.data.domain.Page;

import java.util.Map;

public interface ProductService {
    Product addProduct(CreateProductDto productDto);

    Map<String, Object> getProductsPagination(int pageNumber, int pageSize, String sort);
}