package com.example.order.service;

import com.example.order.dto.OrderDto;
import com.example.order.dto.OrderItemDto;
import com.example.order.entity.Order;
import com.example.order.entity.OrderItem;
import com.example.order.repository.OrderRepository;
import com.example.order.service.usecase.CreateOrderUseCase;
import com.example.order.service.usecase.FetchOrderQuery;
import com.example.order.service.usecase.ProcessExcelOrdersUseCase;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public class OrderService implements
        FetchOrderQuery,
        CreateOrderUseCase,
        ProcessExcelOrdersUseCase
{
    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    public OrderDto createOrder(OrderDto orderDto) {
        if (orderDto.orderItems() == null || orderDto.orderItems().isEmpty()) {
            throw new IllegalArgumentException("Order items must not be empty.");
        }
        Order order = new Order(orderDto.customerName(), orderDto.customerAddress());
        List<OrderItem> orderItemList = orderDto.orderItems()
                .stream().map(
                        orderItemDto -> new OrderItem(
                                order,
                                orderItemDto.productName(),
                                orderItemDto.quantity()
                        )
                ).toList();
        order.setOrderItems(orderItemList);
        orderRepository.save(order);
        return toOrderDto(order);
    }

    @Override
    public void processExcelOrders(MultipartFile file) throws IOException {

    }

    @Override
    public OrderDto fetchOrder(Long orderId) throws ChangeSetPersister.NotFoundException {
        Order order = orderRepository.findById(orderId).orElseThrow(
                ChangeSetPersister.NotFoundException::new
        );
        return toOrderDto(order);
    }

    @Override
    public List<OrderDto> fetchAllOrders() {
        List<Order> orders = orderRepository.findAll();
        return orders.stream().map(this::toOrderDto).toList();
    }

    private OrderDto toOrderDto(Order order) {
        return new OrderDto(
                order.getId(),
                order.getCustomerName(),
                order.getCustomerAddress(),
                order.getOrderItems().stream().map(
                        orderItem -> new OrderItemDto(
                                orderItem.getId(),
                                orderItem.getProductName(),
                                orderItem.getQuantity()
                        )
                ).toList()
        );
    }
}
