package com.example.order.service.usecase;

import com.example.order.dto.OrderDto;
import org.springframework.data.crossstore.ChangeSetPersister;

import java.util.List;

public interface FetchOrderQuery {
    OrderDto fetchOrder(Long orderId) throws ChangeSetPersister.NotFoundException;

    List<OrderDto> fetchAllOrders();
}
