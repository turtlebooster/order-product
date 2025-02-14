package com.example.order.dto.response;

public record OrderItemResponse(Long orderItemId, Long productId, String productName, Long quantity) {
}
