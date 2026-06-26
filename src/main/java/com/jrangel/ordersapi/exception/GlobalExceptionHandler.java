package com.jrangel.ordersapi.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiErrorResponse handleUserNotFound(UserNotFoundException exception) {
        return new ApiErrorResponse(
                exception.getMessage(),
                "USER_NOT_FOUND",
                LocalDateTime.now()
        );
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiErrorResponse handleEmailAlreadyExists(EmailAlreadyExistsException exception) {
        return new ApiErrorResponse(
                exception.getMessage(),
                "EMAIL_ALREADY_EXISTS",
                LocalDateTime.now()
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiErrorResponse handleValidationException(MethodArgumentNotValidException exception) {
        String message = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));

        return new ApiErrorResponse(
                message,
                "VALIDATION_ERROR",
                LocalDateTime.now()
        );
    }

    @ExceptionHandler(ProductNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiErrorResponse handleProductNotFound(ProductNotFoundException exception) {
        return new ApiErrorResponse(
                exception.getMessage(),
                "PRODUCT_NOT_FOUND",
                LocalDateTime.now()
        );
    }

    @ExceptionHandler(OrderNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiErrorResponse handleOrderNotFound(OrderNotFoundException exception) {
        return new ApiErrorResponse(
                exception.getMessage(),
                "ORDER_NOT_FOUND",
                LocalDateTime.now()
        );
    }

    @ExceptionHandler(InsufficientStockException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiErrorResponse handleInsufficientStock(InsufficientStockException exception) {
        return new ApiErrorResponse(
                exception.getMessage(),
                "INSUFFICIENT_STOCK",
                LocalDateTime.now()
        );
    }

    @ExceptionHandler(InactiveProductException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiErrorResponse handleInactiveProduct(InactiveProductException exception) {
        return new ApiErrorResponse(
                exception.getMessage(),
                "INACTIVE_PRODUCT",
                LocalDateTime.now()
        );
    }

    @ExceptionHandler(AuthUserAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiErrorResponse handleAuthUserAlreadyExists(AuthUserAlreadyExistsException exception) {
        return new ApiErrorResponse(
                exception.getMessage(),
                "AUTH_USER_ALREADY_EXISTS",
                LocalDateTime.now()
        );
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ApiErrorResponse handleInvalidCredentials(InvalidCredentialsException exception) {
        return new ApiErrorResponse(
                exception.getMessage(),
                "INVALID_CREDENTIALS",
                LocalDateTime.now()
        );
    }
}