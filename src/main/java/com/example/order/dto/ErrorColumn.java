package com.example.order.dto;

public record ErrorColumn(
        int columnNumber,
        String reason
) {
    public ErrorColumn {
        if (columnNumber < 0) {
            throw new IllegalArgumentException("columnNumber must be greater than or equal to 0");
        }
        if (reason == null) {
            throw new IllegalArgumentException("reason must not be null");
        }
    }
}
