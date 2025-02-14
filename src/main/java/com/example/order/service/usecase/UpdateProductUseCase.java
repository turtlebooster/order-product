package com.example.order.service.usecase;

import com.example.order.dto.ProductDto;

import java.util.List;

public interface UpdateProductUseCase {
    ProductDto updateProduct(ProductDto productDto);

    List<ProductDto> updateProducts(List<ProductDto> productDtos);
}
