package com.example.order.dto;

import com.example.order.dto.response.ProductResponse;

import java.util.List;

public record ProductDto(Long productId, String productName, Long stock) {
    public ProductResponse toResponse() {
        return new ProductResponse(productId(), productName(), stock());
    }
}
