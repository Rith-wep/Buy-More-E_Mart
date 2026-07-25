package com.buymore.backend.dto;

import com.buymore.backend.entity.User;

import java.time.LocalDateTime;

public record UserResponse(
        Long id,
        String name,
        String email,
        User.Role role,
        LocalDateTime createdAt
) {
}
