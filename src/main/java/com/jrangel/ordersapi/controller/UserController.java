package com.jrangel.ordersapi.controller;

import com.jrangel.ordersapi.dto.CreateUserRequest;
import com.jrangel.ordersapi.dto.UserResponse;
import com.jrangel.ordersapi.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@Tag(name = "Products", description = "Operaciones para administrar productos")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "Crear usuario")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse create(@Valid @RequestBody CreateUserRequest request) {
        return userService.create(request);
    }

    @Operation(summary = "Consultar usuario")
    @GetMapping("/{id}")
    public UserResponse findById(@PathVariable Long id) {
        return userService.findById(id);
    }

    @Operation(summary = "Consultar usuarios")
    @GetMapping
    public List<UserResponse> findAll() {
        return userService.findAll();
    }
}