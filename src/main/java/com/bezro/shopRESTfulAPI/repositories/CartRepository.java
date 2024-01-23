package com.bezro.shopRESTfulAPI.repositories;

import com.bezro.shopRESTfulAPI.entities.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<CartItem, Long> {
    Optional<CartItem> findByProduct_Id(Long productId);

    Optional<List<CartItem>> findByUser_Id(Long userId);
}
