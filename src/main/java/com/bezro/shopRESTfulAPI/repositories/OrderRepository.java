package com.bezro.shopRESTfulAPI.repositories;

import com.bezro.shopRESTfulAPI.entities.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
}