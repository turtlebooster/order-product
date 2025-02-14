package com.example.order.dto;

import java.util.List;

public record  BulkUploadPreviewResult(
        List<String> headers,
        List<List<String>> rows,
        List<ErrorColumn> errors
) {
    public BulkUploadPreviewResult {
        if (headers == null) {
            throw new IllegalArgumentException("headers must not be null");
        }
        if (rows == null) {
            throw new IllegalArgumentException("rows must not be null");
        }
        if (errors == null) {
            throw new IllegalArgumentException("errors must not be null");
        }
    }
}
