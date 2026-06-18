package com.jrangel.ordersapi.controller;

import com.jrangel.ordersapi.dto.CreateProductRequest;
import com.jrangel.ordersapi.dto.PageResponse;
import com.jrangel.ordersapi.dto.ProductResponse;
import com.jrangel.ordersapi.dto.UpdateProductRequest;
import com.jrangel.ordersapi.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductResponse create(@Valid @RequestBody CreateProductRequest request) {
        return productService.create(request);
    }

    @GetMapping("/{id}")
    public ProductResponse findById(@PathVariable Long id) {
        return productService.findById(id);
    }

    @GetMapping
    public PageResponse<ProductResponse> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by("createdAt").descending()
        );

        return productService.findAllActive(pageable);
    }

    @PutMapping("/{id}")
    public ProductResponse update(
            @PathVariable Long id,
            @Valid @RequestBody UpdateProductRequest request
    ) {
        return productService.update(id, request);
    }

    @PatchMapping("/{id}/deactivate")
    public ProductResponse deactivate(@PathVariable Long id) {
        return productService.deactivate(id);
    }

    @PatchMapping("/{id}/activate")
    public ProductResponse activate(@PathVariable Long id) {
        return productService.activate(id);
    }
}