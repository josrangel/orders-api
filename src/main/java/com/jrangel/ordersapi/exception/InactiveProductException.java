package com.jrangel.ordersapi.exception;

public class InactiveProductException extends RuntimeException {

    public InactiveProductException(Long productId) {
        super("El producto " + productId + " está inactivo y no puede venderse");
    }
}