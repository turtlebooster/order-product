package com.example.order.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record ProductRequest(
        Long productId,
        @NotNull
        @NotEmpty
        String productName,
        @NotNull
        Long stock
) {
        public ProductRequest {
                if (stock <= 0) {
                        throw new IllegalArgumentException("Stock must be greater than 0");
                }
        }
}
