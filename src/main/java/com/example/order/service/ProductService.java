package com.example.order.service;

import com.example.order.dto.ProductDto;
import com.example.order.entity.Product;
import com.example.order.repository.ProductRepository;
import com.example.order.service.usecase.CreateProductUseCase;
import com.example.order.service.usecase.FetchProductQuery;
import com.example.order.service.usecase.UpdateProductUseCase;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ProductService implements
        FetchProductQuery,
        CreateProductUseCase,
        UpdateProductUseCase
{
    private final ProductRepository productRepository;

    @PersistenceContext
    private EntityManager entityManager;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public ProductDto createProduct(ProductDto productDto) {
        Product product = new Product(productDto.productName(), productDto.stock());
        productRepository.save(product);
        return toProductDto(product);
    }

    @Override
    public List<ProductDto> createProducts(List<ProductDto> productDtos) {
        List<Product> products = productDtos.stream()
                .map(productDto -> new Product(productDto.productName(), productDto.stock()))
                .toList();
        productRepository.saveAll(products);
        return products.stream()
                .map(this::toProductDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ProductDto fetchProduct(Long productId) throws ChangeSetPersister.NotFoundException {
        Product product = productRepository.findById(productId)
                .orElseThrow(ChangeSetPersister.NotFoundException::new);
        return toProductDto(product);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductDto> fetchAllProducts() {
        return productRepository.findAll().stream()
                .map(this::toProductDto)
                .toList();
    }

    private ProductDto toProductDto(Product product) {
        return new ProductDto(product.getId(), product.getName(), product.getStock());
    }

    @Override
    public ProductDto updateProduct(ProductDto productDto) {
        Product product = productRepository.findById(productDto.productId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        product.modifyProduct(productDto.productName(), productDto.stock());
        productRepository.save(product);
        return toProductDto(product);
    }

    @Override
    public List<ProductDto> updateProducts(List<ProductDto> productDtos) {
        List<Product> products = productDtos.stream()
                .map(productDto -> productRepository.findById(productDto.productId())
                        .orElseThrow(() -> new RuntimeException("Product not found"))
                        .modifyProduct(productDto.productName(), productDto.stock()))
                .toList();
        productRepository.saveAll(products);
        return products.stream()
                .map(this::toProductDto)
                .toList();
    }
}
