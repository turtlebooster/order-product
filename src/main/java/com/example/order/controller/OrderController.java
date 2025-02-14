package com.example.order.controller;

import com.example.order.dto.OrderDto;
import com.example.order.dto.OrderItemDto;
import com.example.order.dto.request.OrderRequest;
import com.example.order.dto.response.OrderResponse;
import com.example.order.dto.response.SimpleIdResponse;
import com.example.order.service.usecase.CreateOrderUseCase;
import com.example.order.service.usecase.FetchOrderQuery;
import com.example.order.service.usecase.ProcessExcelOrdersUseCase;
import jakarta.validation.Valid;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    private final FetchOrderQuery fetchOrderQuery;
    private final CreateOrderUseCase createOrderUseCase;
    private final ProcessExcelOrdersUseCase processExcelOrdersUseCase;

    public OrderController(FetchOrderQuery fetchOrderQuery, CreateOrderUseCase createOrderUseCase, ProcessExcelOrdersUseCase processExcelOrdersUseCase) {
        this.fetchOrderQuery = fetchOrderQuery;
        this.createOrderUseCase = createOrderUseCase;
        this.processExcelOrdersUseCase = processExcelOrdersUseCase;
    }

    @GetMapping
    public ResponseEntity<List<OrderResponse>> getOrders() {
        return ResponseEntity.ok(
                fetchOrderQuery.fetchAllOrders()
                        .stream()
                        .map(OrderDto::toResponse)
                        .toList()
        );
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse> getOrder(@PathVariable Long orderId) throws ChangeSetPersister.NotFoundException {
        return ResponseEntity.ok(
                fetchOrderQuery.fetchOrder(orderId).toResponse()
        );
    }

    @PostMapping
    public ResponseEntity<SimpleIdResponse> createOrder(@Valid @RequestBody OrderRequest orderRequest) {
        OrderDto order = createOrderUseCase.createOrder(
                new OrderDto(
                        null,
                        orderRequest.customerName(),
                        orderRequest.customerAddress(),
                        orderRequest.orderItems().stream().map(
                                orderItemRequest -> new OrderItemDto(
                                        null,
                                        orderItemRequest.productId(),
                                        null,
                                        orderItemRequest.quantity()
                                )
                        ).toList()
                )
        );
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new SimpleIdResponse(order.orderId()));
    }

    @PostMapping(value = "/excel-bulk", consumes = (MediaType.MULTIPART_FORM_DATA_VALUE))
    public ResponseEntity<String> uploadOrdersByExcel(@RequestParam("file") MultipartFile file) {
        try {
            processExcelOrdersUseCase.processExcelOrders(file);
            return ResponseEntity.ok("File has been successfully processed.");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while processing the file.");
        }
    }
}
