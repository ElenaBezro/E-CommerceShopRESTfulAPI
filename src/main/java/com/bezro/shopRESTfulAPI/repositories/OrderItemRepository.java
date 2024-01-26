package com.bezro.shopRESTfulAPI.repositories;

import com.bezro.shopRESTfulAPI.entities.OrderItem;
import com.bezro.shopRESTfulAPI.entities.OrderItemId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, OrderItemId> {
    List<OrderItem> findAllByOrder_Id(Long orderId);
}