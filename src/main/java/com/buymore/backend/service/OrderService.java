package com.buymore.backend.service;

import com.buymore.backend.dto.AddressResponse;
import com.buymore.backend.dto.OrderItemRequest;
import com.buymore.backend.dto.OrderItemResponse;
import com.buymore.backend.dto.OrderRequest;
import com.buymore.backend.dto.OrderResponse;
import com.buymore.backend.dto.OrderStatusUpdateRequest;
import com.buymore.backend.dto.PaymentStatusUpdateRequest;
import com.buymore.backend.entity.Address;
import com.buymore.backend.entity.Order;
import com.buymore.backend.entity.OrderItem;
import com.buymore.backend.entity.Product;
import com.buymore.backend.entity.User;
import com.buymore.backend.exception.ForbiddenException;
import com.buymore.backend.exception.InsufficientStockException;
import com.buymore.backend.exception.ResourceNotFoundException;
import com.buymore.backend.repository.AddressRepository;
import com.buymore.backend.repository.OrderRepository;
import com.buymore.backend.repository.ProductRepository;
import com.buymore.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final AddressRepository addressRepository;
    private final UserRepository userRepository;

    @Transactional
    public OrderResponse create(Long customerId, OrderRequest request) {
        User customer = userRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + customerId));

        Address address = addressRepository.findById(request.addressId())
                .orElseThrow(() -> new ResourceNotFoundException("Address not found: " + request.addressId()));

        if (!address.getCustomer().getId().equals(customerId)) {
            throw new ForbiddenException("Address does not belong to this user");
        }

        Order order = Order.builder()
                .orderCode(generateOrderCode())
                .customer(customer)
                .address(address)
                .totalAmount(BigDecimal.ZERO)
                .build();

        BigDecimal total = BigDecimal.ZERO;

        for (OrderItemRequest itemRequest : request.items()) {
            Product product = productRepository.findById(itemRequest.productId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + itemRequest.productId()));

            if (product.getQuantity() < itemRequest.quantity()) {
                throw new InsufficientStockException(
                        "Not enough stock for " + product.getName() + " (available: " + product.getQuantity() + ")");
            }

            BigDecimal unitPrice = product.getSpecialPrice() != null ? product.getSpecialPrice() : product.getPrice();
            BigDecimal subtotal = unitPrice.multiply(BigDecimal.valueOf(itemRequest.quantity()));

            order.getItems().add(OrderItem.builder()
                    .order(order)
                    .product(product)
                    .productName(product.getName())
                    .unitPrice(unitPrice)
                    .quantity(itemRequest.quantity())
                    .build());

            product.setQuantity(product.getQuantity() - itemRequest.quantity());
            product.setSold(product.getSold() + itemRequest.quantity());

            total = total.add(subtotal);
        }

        order.setTotalAmount(total);

        return toResponse(orderRepository.save(order));
    }

    public OrderResponse getByCode(String orderCode) {
        return toResponse(orderRepository.findByOrderCode(orderCode)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found: " + orderCode)));
    }

    public Page<OrderResponse> getByCustomer(Long customerId, Pageable pageable) {
        return orderRepository.findByCustomerId(customerId, pageable).map(this::toResponse);
    }

    public List<OrderResponse> getPendingPayments() {
        return orderRepository.findByPaymentStatus(Order.PaymentStatus.PENDING).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public OrderResponse updateStatus(Long orderId, OrderStatusUpdateRequest request) {
        Order order = findOrThrow(orderId);
        order.setStatus(request.status());
        return toResponse(order);
    }

    @Transactional
    public OrderResponse updatePaymentStatus(Long orderId, PaymentStatusUpdateRequest request) {
        Order order = findOrThrow(orderId);
        order.setPaymentStatus(request.paymentStatus());
        return toResponse(order);
    }

    private Order findOrThrow(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found: " + orderId));
    }

    private String generateOrderCode() {
        int suffix = ThreadLocalRandom.current().nextInt(1000, 9999);
        return "ORD-" + System.currentTimeMillis() + suffix;
    }

    private OrderResponse toResponse(Order order) {
        List<OrderItemResponse> items = order.getItems().stream()
                .map(item -> new OrderItemResponse(
                        item.getId(),
                        item.getProduct() != null ? item.getProduct().getId() : null,
                        item.getProductName(),
                        item.getUnitPrice(),
                        item.getQuantity(),
                        item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity()))
                ))
                .toList();

        AddressResponse addressResponse = order.getAddress() != null
                ? new AddressResponse(
                        order.getAddress().getId(),
                        order.getAddress().getFullName(),
                        order.getAddress().getPhone(),
                        order.getAddress().getCourierName(),
                        order.getAddress().getLocation())
                : null;

        return new OrderResponse(
                order.getId(),
                order.getOrderCode(),
                order.getStatus(),
                order.getPaymentStatus(),
                order.getTotalAmount(),
                order.getReceiptUrl(),
                addressResponse,
                items,
                order.getCreatedAt()
        );
    }
}
