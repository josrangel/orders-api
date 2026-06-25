package com.jrangel.ordersapi.dto;

public record AuthResponse(
        String token,
        String tokenType,
        Long userId,
        String email,
        String role
) {
}