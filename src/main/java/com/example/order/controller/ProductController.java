package com.example.order.controller;

import com.example.order.dto.ProductDto;
import com.example.order.dto.request.ProductRequest;
import com.example.order.dto.response.ProductResponse;
import com.example.order.dto.response.SimpleIdResponse;
import com.example.order.service.usecase.CreateProductUseCase;
import com.example.order.service.usecase.FetchProductQuery;
import com.example.order.service.usecase.UpdateProductUseCase;
import jakarta.validation.Valid;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    private final FetchProductQuery fetchProductQuery;
    private final CreateProductUseCase createProductUseCase;
    private final UpdateProductUseCase updateProductUseCase;

    public ProductController(FetchProductQuery fetchProductQuery, CreateProductUseCase createProductUseCase, UpdateProductUseCase updateProductUseCase) {
        this.fetchProductQuery = fetchProductQuery;
        this.createProductUseCase = createProductUseCase;
        this.updateProductUseCase = updateProductUseCase;
    }

    @GetMapping
    public ResponseEntity<List<ProductResponse>> getProducts() {
        return ResponseEntity.ok(
                fetchProductQuery.fetchAllProducts()
                        .stream()
                        .map(ProductDto::toResponse)
                        .toList()
        );
    }

    @GetMapping("/{productId}")
    public ResponseEntity<ProductResponse> getProduct(@PathVariable Long productId) throws ChangeSetPersister.NotFoundException {
        return ResponseEntity.ok(
                fetchProductQuery.fetchProduct(productId).toResponse()
        );
    }

    @PostMapping
    public ResponseEntity<SimpleIdResponse> createProduct(@Valid @RequestBody ProductDto productDto) {
        ProductDto product = createProductUseCase.createProduct(productDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(new SimpleIdResponse(product.productId()));
    }

    @PostMapping("/bulk")
    public ResponseEntity<List<SimpleIdResponse>> createProducts(@Valid @RequestBody List<ProductDto> productDtos) {
        List<ProductDto> products = createProductUseCase.createProducts(productDtos);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                products.stream()
                        .map(product -> new SimpleIdResponse(product.productId()))
                        .toList()
        );
    }

    @PutMapping("/{productId}")
    public ResponseEntity<ProductResponse> updateProduct(@PathVariable Long productId, @Valid @RequestBody ProductRequest request) throws ChangeSetPersister.NotFoundException {
        ProductDto product = updateProductUseCase.updateProduct(
                new ProductDto(productId, request.productName(), request.stock())
        );
        return ResponseEntity.ok(product.toResponse());
    }

    @PutMapping("/bulk")
    public ResponseEntity<List<ProductResponse>> updateProducts(@Valid @RequestBody List<ProductRequest> requests) {
        List<ProductDto> products = updateProductUseCase.updateProducts(
                requests.stream().map(
                        product -> new ProductDto(product.productId(), product.productName(), product.stock())
                ).toList()
        );
        return ResponseEntity.ok(products.stream().map(ProductDto::toResponse).toList());
    }
}
