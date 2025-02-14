package com.example.order.dto.response;

public record OrderItemResponse(Long orderItemId, String productName, int quantity) {
}
