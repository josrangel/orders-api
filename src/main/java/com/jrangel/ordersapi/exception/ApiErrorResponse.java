package com.jrangel.ordersapi.exception;

import java.time.LocalDateTime;

public record ApiErrorResponse(
        String message,
        String code,
        LocalDateTime timestamp
) {
}