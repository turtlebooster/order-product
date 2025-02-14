package com.example.order.dto.response;

import lombok.Getter;

import java.util.List;

public record OrderResponse(Long orderId, String customerName, String customerAddress, List<OrderItemResponse> orderItems) {}
