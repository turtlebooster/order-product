package com.example.order.dto;

import com.example.order.dto.response.OrderItemResponse;
import com.example.order.dto.response.OrderResponse;
import lombok.Getter;

import java.util.List;

public record OrderDto(Long orderId, String customerName, String customerAddress, List<OrderItemDto> orderItems) {
    public OrderResponse toResponse() {
        return new OrderResponse(
                orderId,
                customerName,
                customerAddress,
                orderItems.stream()
                        .map(OrderItemDto::toResponse)
                        .toList()
                );
    }
}
