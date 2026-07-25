package com.buymore.backend.repository;

import com.buymore.backend.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    Optional<Order> findByOrderCode(String orderCode);

    Page<Order> findByCustomerId(Long customerId, Pageable pageable);

    List<Order> findByPaymentStatus(Order.PaymentStatus paymentStatus);
}
