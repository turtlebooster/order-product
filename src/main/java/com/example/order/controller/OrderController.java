package com.example.order.controller;

import com.example.order.dto.OrderDto;
import com.example.order.dto.OrderItemDto;
import com.example.order.dto.request.OrderRequest;
import com.example.order.dto.response.OrderResponse;
import com.example.order.dto.response.SimpleIdResponse;
import com.example.order.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    public ResponseEntity<List<OrderDto>> getOrders() {
        return ResponseEntity.ok(orderService.fetchAllOrders());
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse> getOrder(@PathVariable Long orderId) throws ChangeSetPersister.NotFoundException {
        return ResponseEntity.ok(orderService.fetchOrder(orderId).toResponse());
    }

    @PostMapping
    public ResponseEntity<SimpleIdResponse> createOrder(@Valid @RequestBody OrderRequest orderRequest) {
        OrderDto order = orderService.createOrder(
                new OrderDto(
                        null,
                        orderRequest.customerName(),
                        orderRequest.customerAddress(),
                        orderRequest.orderItems().stream().map(
                                orderItemRequest -> new OrderItemDto(
                                        null,
                                        orderItemRequest.productName(),
                                        orderItemRequest.quantity()
                                )
                        ).toList()
                )
        );
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new SimpleIdResponse(order.orderId()));
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadOrders(@RequestParam("file") MultipartFile file) {
        throw new UnsupportedOperationException("Not implemented yet");
//        try {
//            orderService.processExcelFile(file);
//            return ResponseEntity.ok("File has been successfully processed.");
//        } catch (IOException e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while processing the file.");
//        }
    }
}
