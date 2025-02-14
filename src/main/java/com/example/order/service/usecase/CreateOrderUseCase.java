package com.example.order.service.usecase;

import com.example.order.dto.OrderDto;

public interface CreateOrderUseCase {
    OrderDto createOrder(OrderDto orderDto);
}
