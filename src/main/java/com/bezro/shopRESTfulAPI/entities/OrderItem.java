package com.bezro.shopRESTfulAPI.entities;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "order_items")
public class OrderItem {
    @EmbeddedId
    private OrderItemId orderItemId;

    @Column(nullable = false)
    private double quantity;

    @Column(nullable = false)
    private double price;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id", insertable = false, updatable = false, nullable = false, referencedColumnName = "id")
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", insertable = false, updatable = false, nullable = false, referencedColumnName = "id")
    private Product product;
}
