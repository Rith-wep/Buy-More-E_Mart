package com.buymore.backend.dto;

import com.buymore.backend.entity.Order;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderResponse(
        Long id,
        String orderCode,
        Order.Status status,
        Order.PaymentStatus paymentStatus,
        BigDecimal totalAmount,
        String receiptUrl,
        AddressResponse address,
        List<OrderItemResponse> items,
        LocalDateTime createdAt
) {
}
