package com.example.order.dto;

public class OrderItemDto {
    private final Long orderItemId;
    private final String productName;
    private final int quantity;

    public OrderItemDto(Long orderItemId, String productName, int quantity) {
        this.orderItemId = orderItemId;
        this.productName = productName;
        this.quantity = quantity;
    }

    public Long getOrderItemId() {
        return orderItemId;
    }

    public String getProductName() {
        return productName;
    }

    public int getQuantity() {
        return quantity;
    }
}
