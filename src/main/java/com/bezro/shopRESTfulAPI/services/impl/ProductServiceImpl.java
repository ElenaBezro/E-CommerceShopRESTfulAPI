package com.bezro.shopRESTfulAPI.services.impl;

import com.bezro.shopRESTfulAPI.dtos.CreateProductDto;
import com.bezro.shopRESTfulAPI.entities.Product;
import com.bezro.shopRESTfulAPI.exceptions.InvalidMethodArgumentsException;
import com.bezro.shopRESTfulAPI.exceptions.NoContentException;
import com.bezro.shopRESTfulAPI.exceptions.NotEnoughProductStockException;
import com.bezro.shopRESTfulAPI.repositories.ProductRepository;
import com.bezro.shopRESTfulAPI.services.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;

    public Product addProduct(CreateProductDto productDto) {
        Product product = new Product();
        product.setName(productDto.getName());
        product.setDescription(productDto.getDescription());
        product.setPrice(productDto.getPrice());
        product.setQuantity(productDto.getQuantity());
        return productRepository.save(product);
    }

    public Product findById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new InvalidMethodArgumentsException(
                        String.format("Product with id: %d does not exist", id)));
    }

    public Product updateProduct(Long id, CreateProductDto productDto) {
        Product product = findById(id);
        //TODO: use ModelMapper to map data from DTO to the entity
        //    modelMapper.map(productDto, product) ?
        product.setName(productDto.getName());
        product.setDescription(productDto.getDescription());
        product.setPrice(productDto.getPrice());
        product.setQuantity(productDto.getQuantity());
        return productRepository.save(product);
    }

    public void deleteProduct(Long id) {
        Product product = findById(id);
        productRepository.delete(product);
    }

    public Map<String, Object> getProductsPagination(int pageNumber, int pageSize, String sort) {
        Pageable pageable = null;
        if (sort != null) {
            pageable = PageRequest.of(pageNumber, pageSize, Sort.Direction.ASC, sort);
        } else {
            pageable = PageRequest.of(pageNumber, pageSize);
        }
        Page<Product> productPage = productRepository.findAll(pageable);

        if (!productPage.hasContent()) {
            throw new NoContentException("No Content");
        }

        Map<String, Object> response = new HashMap<>();

        response.put("products", productPage.getContent());
        response.put("currentPage", productPage.getNumber());
        response.put("totalItems", productPage.getTotalElements());
        response.put("totalPages", productPage.getTotalPages());

        return response;
    }

    public void decreaseProductStock(Long productId, double decrementAmount) {
        Product product = findById(productId);
        if (product.getQuantity() < decrementAmount) {
            throw new NotEnoughProductStockException(List.of("Not enough product with id:" + productId));
        }
        product.setQuantity(product.getQuantity() - decrementAmount);
        productRepository.save(product);
    }
}
