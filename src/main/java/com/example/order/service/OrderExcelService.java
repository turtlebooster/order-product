package com.example.order.service;

import com.example.order.dto.BulkUploadPreviewResult;
import com.example.order.dto.OrderDto;
import com.example.order.dto.OrderItemDto;
import com.example.order.entity.Order;
import com.example.order.entity.OrderItem;
import com.example.order.entity.Product;
import com.example.order.repository.OrderRepository;
import com.example.order.repository.ProductRepository;
import com.example.order.service.usecase.CreateOrderUseCase;
import com.example.order.service.usecase.FetchOrderQuery;
import com.example.order.service.usecase.ProcessExcelOrdersUseCase;
import lombok.val;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.graalvm.collections.Pair;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

@Service
@Transactional
public class OrderExcelService implements
        ProcessExcelOrdersUseCase
{
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    public OrderExcelService(OrderRepository orderRepository, ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
    }

    @Override
    public void processExcelOrders(MultipartFile file) throws IOException {
        try (InputStream inputStream = file.getInputStream(); Workbook workbook = new XSSFWorkbook(inputStream)) {
            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rowIterator = sheet.iterator();
            rowIterator.next();

            Map<String, Order> orderMap = new HashMap<>();

            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                String customerName = row.getCell(0).getStringCellValue();
                String customerAddress = row.getCell(1).getStringCellValue();
                String orderKey = customerName + "-" + customerAddress;

                Order order = orderMap.getOrDefault(orderKey, new Order(customerName, customerAddress));

                for (int i = 2; i < row.getLastCellNum(); i += 2) {
                    Long productId = (long) row.getCell(i).getNumericCellValue();
                    Long quantity = (long) row.getCell(i + 1).getNumericCellValue();
                    Product product = productRepository.findById(productId)
                            .orElseThrow(() -> new IllegalArgumentException("Invalid product ID: " + productId));

                    product.decreaseStock(quantity);
                    order.addOrderItem(new OrderItem(order, product, quantity));
                }

                orderMap.put(orderKey, order);
            }

            orderRepository.saveAll(orderMap.values());
        }
    }

    @Override
    public BulkUploadPreviewResult previewExcelOrders(MultipartFile file) throws IOException {
        
    }

    private Pair<Workbook, Sheet> createWorkSheet(MultipartFile file) throws IOException {
        Workbook workbook = WorkbookFactory.create(file.getInputStream());
        return Pair.create(workbook, workbook.getSheetAt(0));
    }
}


enum OrderProductExcelUploadHeader {
    CUSTOMER_NAME("주문자 이름", 0),
    CUSTOMER_ADDRESS("주문자 주소", 1),
    PRODUCT_ID("상품 ID", 2),
    ORDER_QUANTITY("주문 수량", 3),
    ;

    public final String str;
    public final int colNum;

    OrderProductExcelUploadHeader(String str, int colNum) {
        this.str = str;
        this.colNum = colNum;
    }

    static OrderProductExcelUploadHeader fromValue(String v) {
        for (OrderProductExcelUploadHeader header : values()) {
            if (header.str.equalsIgnoreCase(v)) {
                return header;
            }
        }
        return null;
    }
}