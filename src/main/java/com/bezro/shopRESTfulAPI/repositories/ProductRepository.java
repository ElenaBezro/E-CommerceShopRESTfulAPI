package com.bezro.shopRESTfulAPI.repositories;

import com.bezro.shopRESTfulAPI.entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
}
