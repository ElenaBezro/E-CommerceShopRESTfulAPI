package com.bezro.shopRESTfulAPI.services;

import com.bezro.shopRESTfulAPI.dtos.CreateProductDto;
import com.bezro.shopRESTfulAPI.entities.Product;

public interface ProductService {
    Product addProduct(CreateProductDto productDto);

}
