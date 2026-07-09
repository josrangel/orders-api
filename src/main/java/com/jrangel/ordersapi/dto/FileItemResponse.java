package com.jrangel.ordersapi.dto;

public record FileItemResponse(
        String fileName,
        long size,
        String lastModified
) {
}