package com.example.order.dto;

import com.example.order.dto.response.OrderItemResponse;
import lombok.Getter;

public record OrderItemDto(Long orderItemId, Long productId, String productName, Long quantity) {
    public OrderItemResponse toResponse() {
        return new OrderItemResponse(orderItemId, productId, productName, quantity);
    }
}

