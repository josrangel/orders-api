package com.jrangel.ordersapi.dto;

public record FileUploadResponse(
        String fileName,
        String bucket,
        String contentType,
        long size
) {
}