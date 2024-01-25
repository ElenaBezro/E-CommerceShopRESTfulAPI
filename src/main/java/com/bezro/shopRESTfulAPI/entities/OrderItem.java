package com.bezro.shopRESTfulAPI.entities;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "order_items")
public class OrderItem {
    @EmbeddedId
    private OrderItemId orderItemId = new OrderItemId();

    @Column(nullable = false)
    private double quantity;

    @Column(nullable = false)
    private double price;

    public void setOrder(Order order) {
        orderItemId.setOrder(order);
    }

    public void setProduct(Product product) {
        orderItemId.setProduct(product);
    }
}
