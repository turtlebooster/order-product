package com.example.order.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record OrderItemRequest(
        @NotNull
        @NotEmpty
        String productName,
        @NotNull
        int quantity
) {
        public OrderItemRequest {
                if (quantity <= 0) {
                        throw new IllegalArgumentException("Quantity must be greater than 0");
                }
        }
}
