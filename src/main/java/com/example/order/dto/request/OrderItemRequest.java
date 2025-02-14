package com.example.order.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record OrderItemRequest(
        @NotNull
        Long productId,
        @NotNull
        Long quantity
) {
        public OrderItemRequest {
                if (productId <= 0) {
                        throw new IllegalArgumentException("Product ID must be greater than 0");
                }
                if (quantity <= 0) {
                        throw new IllegalArgumentException("Quantity must be greater than 0");
                }
        }
}
