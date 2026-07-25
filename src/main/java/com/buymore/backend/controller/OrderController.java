package com.buymore.backend.controller;

import com.buymore.backend.dto.OrderRequest;
import com.buymore.backend.dto.OrderResponse;
import com.buymore.backend.dto.OrderStatusUpdateRequest;
import com.buymore.backend.dto.PaymentStatusUpdateRequest;
import com.buymore.backend.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/api/users/{customerId}/orders")
    public ResponseEntity<OrderResponse> create(
            @PathVariable Long customerId,
            @Valid @RequestBody OrderRequest request) {

        OrderResponse response = orderService.create(customerId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/api/users/{customerId}/orders")
    public Page<OrderResponse> getByCustomer(@PathVariable Long customerId, Pageable pageable) {
        return orderService.getByCustomer(customerId, pageable);
    }

    @GetMapping("/api/orders/pending-payments")
    public List<OrderResponse> getPendingPayments() {
        return orderService.getPendingPayments();
    }

    @GetMapping("/api/orders/{orderCode}")
    public ResponseEntity<OrderResponse> getByCode(@PathVariable String orderCode) {
        return ResponseEntity.ok(orderService.getByCode(orderCode));
    }

    @PatchMapping("/api/orders/{id}/status")
    public ResponseEntity<OrderResponse> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody OrderStatusUpdateRequest request) {

        return ResponseEntity.ok(orderService.updateStatus(id, request));
    }

    @PatchMapping("/api/orders/{id}/payment-status")
    public ResponseEntity<OrderResponse> updatePaymentStatus(
            @PathVariable Long id,
            @Valid @RequestBody PaymentStatusUpdateRequest request) {

        return ResponseEntity.ok(orderService.updatePaymentStatus(id, request));
    }
}
