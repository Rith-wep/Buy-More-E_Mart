package com.buymore.backend.dto;

public record AddressResponse(
        Long id,
        String fullName,
        String phone,
        String courierName,
        String location
) {
}
