package com.example.order.service;

import com.example.order.dto.OrderDto;
import com.example.order.dto.OrderItemDto;
import com.example.order.entity.Order;
import com.example.order.entity.OrderItem;
import com.example.order.entity.Product;
import com.example.order.repository.OrderRepository;
import com.example.order.repository.ProductRepository;
import com.example.order.service.usecase.CreateOrderUseCase;
import com.example.order.service.usecase.FetchOrderQuery;
import com.example.order.service.usecase.ProcessExcelOrdersUseCase;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@Transactional
public class OrderService implements
        FetchOrderQuery,
        CreateOrderUseCase
{
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    public OrderService(OrderRepository orderRepository, ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
    }

    @Override
    public OrderDto createOrder(OrderDto orderDto) {
        if (orderDto.orderItems() == null || orderDto.orderItems().isEmpty()) {
            throw new IllegalArgumentException("Order items must not be empty.");
        }
        // 상품 ID 목록
        List<Long> originProductIds = orderDto.orderItems()
                .stream()
                .map(OrderItemDto::productId)
                .toList();
        List<Long> productIds = originProductIds.stream().distinct().toList();
        if (originProductIds.size() != productIds.size()) {
            throw new IllegalArgumentException("Duplicate product ID.");
        }

        // 상품 목록
        List<Product> products = productRepository.findAllByIdsWithLock(productIds);
        if (products.size() != productIds.size()) {
            throw new IllegalArgumentException("Product not found.");
        }

        Order order = new Order(orderDto.customerName(), orderDto.customerAddress());
        List<OrderItem> orderItemList = orderDto.orderItems()
                .stream().map(
                        orderItemDto -> new OrderItem(
                                order,
                                products.stream()
                                        .filter(product -> product.getId().equals(orderItemDto.productId()))
                                        .findFirst()
                                        .orElseThrow(() -> new IllegalArgumentException("Product not found."))
                                        .decreaseStock(orderItemDto.quantity()),
                                orderItemDto.quantity()
                        )
                ).toList();
        order.setOrderItems(orderItemList);
        orderRepository.save(order);
        return toOrderDto(order);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderDto fetchOrder(Long orderId) throws ChangeSetPersister.NotFoundException {
        Order order = orderRepository.findById(orderId).orElseThrow(
                ChangeSetPersister.NotFoundException::new
        );
        return toOrderDto(order);
    }

    @Override
    @Transactional(readOnly = true)
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
                                orderItem.getProduct().getId(),
                                orderItem.getProduct().getName(),
                                orderItem.getQuantity()
                        )
                ).toList()
        );
    }
}
