package com.example.order.dto;

import java.util.List;

public class OrderDto {
    private final Long orderId;
    private final String customerName;
    private final String customerAddress;
    private final List<OrderItemDto> orderItems;

    public OrderDto(Long orderId, String customerName, String customerAddress, List<OrderItemDto> orderItems) {
        this.orderId = orderId;
        this.customerName = customerName;
        this.customerAddress = customerAddress;
        this.orderItems = orderItems;
    }

    public Long getOrderId() {
        return orderId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getCustomerAddress() {
        return customerAddress;
    }

    public List<OrderItemDto> getOrderItems() {
        return orderItems;
    }
}
