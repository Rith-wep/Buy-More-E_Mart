package com.buymore.backend.dto;

import com.buymore.backend.entity.Product;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ProductResponse(
        Long id,
        String name,
        String description,
        String image,
        BigDecimal price,
        BigDecimal discount,
        BigDecimal specialPrice,
        Integer quantity,
        Integer sold,
        Product.Status status,
        CategoryResponse category,
        Long sellerId,
        LocalDateTime createdAt
) {
}
