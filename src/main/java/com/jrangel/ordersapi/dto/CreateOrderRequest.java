package com.jrangel.ordersapi.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record CreateOrderRequest(
        @NotNull(message = "El userId es obligatorio")
        Long userId,

        @NotEmpty(message = "La orden debe tener al menos un producto")
        List<@Valid CreateOrderItemRequest> items
) {
}