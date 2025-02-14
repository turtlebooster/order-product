package com.example.order.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Entity
@Table(name = "products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private Long stock;

    public Product() {}

    public Product(String name, Long stock) {
        this.name = name;
        this.stock = stock;
    }

    public Product modifyProduct(String name, Long stock) {
        this.name = name;
        this.stock = stock;
        return this;
    }

    public Product decreaseStock(Long quantity) {
        if (this.stock < quantity) {
            throw new IllegalArgumentException(
                    """
                    quantity cannot be greater than stock,
                    productName: %s,
                    stock: %d,
                    quantity: %d
                    """.formatted(this.name, this.stock, quantity)
            );
        }
        this.stock -= quantity;
        return this;
    }
}