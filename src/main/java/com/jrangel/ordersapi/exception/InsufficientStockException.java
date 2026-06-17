package com.jrangel.ordersapi.exception;

public class InsufficientStockException extends RuntimeException {

    public InsufficientStockException(Long productId, Integer availableStock, Integer requestedQuantity) {
        super("Stock insuficiente para el producto " + productId +
                ". Disponible: " + availableStock +
                ", solicitado: " + requestedQuantity);
    }
}