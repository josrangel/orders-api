package com.jrangel.ordersapi.controller;

import com.jrangel.ordersapi.dto.CreateOrderRequest;
import com.jrangel.ordersapi.dto.OrderResponse;
import com.jrangel.ordersapi.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponse create(@Valid @RequestBody CreateOrderRequest request) {
        return orderService.create(request);
    }

    @GetMapping("/{id}")
    public OrderResponse findById(@PathVariable Long id) {
        return orderService.findById(id);
    }

    @GetMapping("/by-user/{userId}")
    public List<OrderResponse> findByUserId(@PathVariable Long userId) {
        return orderService.findByUserId(userId);
    }
}