package com.buymore.backend.dto;

import com.buymore.backend.entity.Order;
import jakarta.validation.constraints.NotNull;

public record OrderStatusUpdateRequest(
        @NotNull Order.Status status
) {
}
