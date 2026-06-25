package com.jrangel.ordersapi.exception;

public class AuthUserAlreadyExistsException extends RuntimeException {

    public AuthUserAlreadyExistsException(String email) {
        super("Ya existe un usuario de autenticación con el email: " + email);
    }
}