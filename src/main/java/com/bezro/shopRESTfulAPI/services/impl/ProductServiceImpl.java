package com.bezro.shopRESTfulAPI.services.impl;

import com.bezro.shopRESTfulAPI.dtos.CreateProductDto;
import com.bezro.shopRESTfulAPI.entities.Product;
import com.bezro.shopRESTfulAPI.exceptions.InsufficientProductStockException;
import com.bezro.shopRESTfulAPI.exceptions.InvalidMethodArgumentsException;
import com.bezro.shopRESTfulAPI.exceptions.NoContentException;
import com.bezro.shopRESTfulAPI.repositories.ProductRepository;
import com.bezro.shopRESTfulAPI.services.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;

    public Product addProduct(CreateProductDto productDto) {
        log.info("Adding new product: {}", productDto.getName());
        Product product = new Product();
        product.setName(productDto.getName());
        product.setDescription(productDto.getDescription());
        product.setPrice(productDto.getPrice());
        product.setQuantity(productDto.getQuantity());
        Product savedProduct = productRepository.save(product);
        log.info("Product added successfully: {}", savedProduct);
        return savedProduct;
    }

    public Product findById(Long id) {
        log.info("Finding product by id: {}", id);
        return productRepository.findById(id)
                .orElseThrow(() -> new InvalidMethodArgumentsException(
                        String.format("Product with id: %d does not exist", id)));
    }

    public Product updateProduct(Long id, CreateProductDto productDto) {
        log.info("Updating product with id: {}", id);
        Product product = findById(id);
        //TODO: use ModelMapper to map data from DTO to the entity
        //    modelMapper.map(productDto, product) ?
        product.setName(productDto.getName());
        product.setDescription(productDto.getDescription());
        product.setPrice(productDto.getPrice());
        product.setQuantity(productDto.getQuantity());
        Product updatedProduct = productRepository.save(product);
        log.info("Product updated successfully: {}", updatedProduct);
        return updatedProduct;
    }

    public void deleteProduct(Long id) {
        log.info("Deleting product with id: {}", id);
        Product product = findById(id);
        productRepository.delete(product);
        log.info("Product deleted successfully: {}", product);
    }

    public Map<String, Object> getProductsPagination(int pageNumber, int pageSize, String sort) {
        log.info("Fetching products for page number: {}, page size: {}, sort: {}", pageNumber, pageSize, sort);
        Pageable pageable = null;
        if (sort != null) {
            pageable = PageRequest.of(pageNumber, pageSize, Sort.Direction.ASC, sort);
        } else {
            pageable = PageRequest.of(pageNumber, pageSize);
        }
        Page<Product> productPage = productRepository.findAll(pageable);

        if (!productPage.hasContent()) {
            log.error("No products found for page number: {}, page size: {}, sort: {}", pageNumber, pageSize, sort);
            throw new NoContentException("No Content");
        }

        Map<String, Object> response = new HashMap<>();

        response.put("products", productPage.getContent());
        response.put("currentPage", productPage.getNumber());
        response.put("totalItems", productPage.getTotalElements());
        response.put("totalPages", productPage.getTotalPages());

        log.info("Fetched products successfully");
        return response;
    }

    public void decreaseProductStock(Long productId, double decrementAmount) {
        log.info("Decreasing stock for product with id: {}, decrement amount: {}", productId, decrementAmount);
        Product product = findById(productId);
        if (product.getQuantity() < decrementAmount) {
            log.error("Not enough stock for product with id: {}, current stock: {}, decrement amount: {}", productId, product.getQuantity(), decrementAmount);
            throw new InsufficientProductStockException("Not enough product with id:" + productId);
        }
        product.setQuantity(product.getQuantity() - decrementAmount);
        productRepository.save(product);
        log.info("Stock decreased successfully");
    }
}
