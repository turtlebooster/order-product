package com.example.order.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "order_items")
public class OrderItem {
    @Id
    @Getter
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Getter
    private String productName;
    @Getter
    private int quantity;

    public OrderItem() {}

    public OrderItem(Order order, String productName, int quantity) {
        this.order = order;
        this.productName = productName;
        this.quantity = quantity;
    }
}
