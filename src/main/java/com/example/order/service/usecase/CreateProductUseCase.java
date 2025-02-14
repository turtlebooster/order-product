package com.example.order.service.usecase;

import com.example.order.dto.ProductDto;

import java.util.List;

public interface CreateProductUseCase {
    ProductDto createProduct(ProductDto productDto);

    List<ProductDto> createProducts(List<ProductDto> productDtos);
}
