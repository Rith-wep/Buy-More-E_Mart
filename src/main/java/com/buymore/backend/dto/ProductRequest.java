package com.buymore.backend.dto;

import com.buymore.backend.entity.Product;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record ProductRequest(
        @NotBlank String name,
        String description,
        String image,
        @NotNull @DecimalMin(value = "0.0", inclusive = true) BigDecimal price,
        @DecimalMin(value = "0.0", inclusive = true) BigDecimal discount,
        @DecimalMin(value = "0.0", inclusive = true) BigDecimal specialPrice,
        @NotNull @Min(0) Integer quantity,
        Product.Status status,
        @NotNull Long categoryId,
        Long sellerId
) {
}
