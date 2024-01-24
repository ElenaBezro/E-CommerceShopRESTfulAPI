package com.bezro.shopRESTfulAPI.entities;

public enum OrderStatus {
    PROCESSING,
    SHIPPED,
    DELIVERED;

    public static OrderStatus getNext(OrderStatus previousStatus) {
        return switch (previousStatus) {
            case PROCESSING -> OrderStatus.SHIPPED;
            case SHIPPED -> OrderStatus.DELIVERED;
            default -> previousStatus;
        };
    }
}
