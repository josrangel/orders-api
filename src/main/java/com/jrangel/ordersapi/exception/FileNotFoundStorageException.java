package com.jrangel.ordersapi.exception;

public class FileNotFoundStorageException extends RuntimeException {

    public FileNotFoundStorageException(String fileName) {
        super("Archivo no encontrado: " + fileName);
    }
}