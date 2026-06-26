package com.jrangel.ordersapi.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank(message = "El email es obligatorio")
        @Email(message = "El email no tiene un formato válido")
        @Size(max = 150, message = "El email no puede tener más de 150 caracteres")
        String email,

        @NotBlank(message = "El password es obligatorio")
        @Size(min = 8, max = 100, message = "El password debe tener entre 8 y 100 caracteres")
        String password
) {
}