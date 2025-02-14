package com.example.order.service.usecase;

import com.example.order.dto.ProductDto;
import org.springframework.data.crossstore.ChangeSetPersister;

import java.util.List;

public interface FetchProductQuery {
    ProductDto fetchProduct(Long productId) throws ChangeSetPersister.NotFoundException;

    List<ProductDto> fetchAllProducts();
}
