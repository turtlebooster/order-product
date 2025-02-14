package com.example.order.controller;

import com.example.order.dto.OrderDto;
import com.example.order.dto.request.OrderRequest;
import com.example.order.dto.response.OrderResponse;
import com.example.order.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@Valid @RequestBody OrderRequest orderRequest) {
        OrderDto order = orderService.createOrder(orderRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(new OrderResponse(order.id, "Order has been successfully registered."));
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadOrders(@RequestParam("file") MultipartFile file) {
        try {
            orderService.processExcelFile(file);
            return ResponseEntity.ok("File has been successfully processed.");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while processing the file.");
        }
    }
}
