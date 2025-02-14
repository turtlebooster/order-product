package com.example.order.service;

import com.example.order.dto.OrderDto;
import com.example.order.dto.OrderItemDto;
import com.example.order.dto.request.OrderRequest;
import com.example.order.entity.Order;
import com.example.order.entity.OrderItem;
import com.example.order.repository.OrderItemRepository;
import com.example.order.repository.OrderRepository;
import com.example.order.service.usecase.CreateOrderUseCase;
import com.example.order.service.usecase.ProcessExcelOrdersUseCase;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public class OrderService implements CreateOrderUseCase, ProcessExcelOrdersUseCase {
    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    public OrderDto createOrder(OrderDto orderDto) {
        if (orderDto.getOrderItems() == null || orderDto.getOrderItems().isEmpty()) {
            throw new IllegalArgumentException("Order items must not be empty.");
        }
        Order order = new Order(orderDto.getCustomerName(), orderDto.getCustomerAddress());
        List<OrderItem> orderItemList = orderDto.getOrderItems()
                .stream().map(
                        orderItemDto -> new OrderItem(
                                order,
                                orderItemDto.getProductName(),
                                orderItemDto.getQuantity()
                        )
                ).toList();
        order.setOrderItems(orderItemList);
        orderRepository.save(order);
        return new OrderDto(
                order.getId(),
                order.getCustomerName(),
                order.getCustomerAddress(),
                orderItemList.stream().map(
                        orderItem -> new OrderItemDto(
                                orderItem.getId(),
                                orderItem.getProductName(),
                                orderItem.getQuantity()
                        )
                ).toList()
        );
    }

    @Override
    public void processExcelOrders(MultipartFile file) throws IOException {

    }
}
