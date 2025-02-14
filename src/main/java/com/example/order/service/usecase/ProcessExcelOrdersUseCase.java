package com.example.order.service.usecase;

import org.springframework.core.io.InputStreamResource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ProcessExcelOrdersUseCase {
    void processExcelOrders(MultipartFile file) throws IOException;

    InputStreamResource downloadExcelTemplate() throws IOException;
}