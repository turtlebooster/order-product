package com.example.order.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record OrderRequest(
        @NotNull
        @NotEmpty
        String customerName,
        @NotNull
        @NotEmpty
        String customerAddress,
        @NotEmpty
        List<OrderItemRequest> orderItems
) {}
