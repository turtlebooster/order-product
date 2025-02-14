package com.example.order.dto;

import com.example.order.dto.response.OrderItemResponse;
import lombok.Getter;

public record OrderItemDto(Long orderItemId, String productName, int quantity) {
    public OrderItemResponse toResponse() {
        return new OrderItemResponse(orderItemId, productName, quantity);
    }
}

