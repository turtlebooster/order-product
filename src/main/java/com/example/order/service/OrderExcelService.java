package com.example.order.service;

import com.example.order.entity.Order;
import com.example.order.entity.OrderItem;
import com.example.order.entity.Product;
import com.example.order.repository.OrderRepository;
import com.example.order.repository.ProductRepository;
import com.example.order.service.usecase.ProcessExcelOrdersUseCase;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

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
            // Skip header
            rowIterator.next();

            Set<Long> productIds = new HashSet<>();
            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                for (int i = 2; i < row.getLastCellNum(); i += 2) {
                    productIds.add((long) row.getCell(i).getNumericCellValue());
                }
            }
            // product map
            Map<Long, Product> productMap = productRepository.findAllByIdsWithLock(productIds.stream().toList())
                    .stream().collect(Collectors.toMap(Product::getId, product -> product));

            // reset iterator
            rowIterator = sheet.iterator();
            rowIterator.next();

            // order map
            Map<String, Order> orderMap = new HashMap<>();
            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                String customerName = row.getCell(0).getStringCellValue();
                String customerAddress = row.getCell(1).getStringCellValue();
                String orderKey = customerName + "-" + customerAddress;

                Order order = orderMap.getOrDefault(orderKey, new Order(customerName, customerAddress));

                for (int j = 2; j < row.getLastCellNum(); j += 2) {
                    Long productId = (long) row.getCell(j).getNumericCellValue();
                    Long quantity = (long) row.getCell(j + 1).getNumericCellValue();
                    Product product = productMap.get(productId);

                    if (product == null) {
                        throw new IllegalArgumentException("Invalid product ID: " + productId);
                    }

                    product.decreaseStock(quantity);
                    order.addOrderItem(new OrderItem(order, product, quantity));
                }

                orderMap.put(orderKey, order);
            }

            orderRepository.saveAll(orderMap.values());
        }
    }

    @Override
    public InputStreamResource downloadExcelTemplate() throws IOException {
        // resources/templates/order_template.xlsx
        Resource resource = new ClassPathResource("templates/order_template.xlsx");
        return new InputStreamResource(resource.getInputStream());
    }
}