package com.jrangel.ordersapi.service;

import com.jrangel.ordersapi.dto.CreateProductRequest;
import com.jrangel.ordersapi.dto.ProductResponse;
import com.jrangel.ordersapi.dto.UpdateProductRequest;
import com.jrangel.ordersapi.entity.ProductEntity;
import com.jrangel.ordersapi.exception.ProductNotFoundException;
import com.jrangel.ordersapi.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Transactional
    public ProductResponse create(CreateProductRequest request) {
        ProductEntity product = new ProductEntity(
                request.name(),
                request.description(),
                request.price(),
                request.stock()
        );

        ProductEntity savedProduct = productRepository.save(product);

        return toResponse(savedProduct);
    }

    @Transactional(readOnly = true)
    public ProductResponse findById(Long id) {
        ProductEntity product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));

        return toResponse(product);
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> findAllActive() {
        return productRepository.findByActiveTrue()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public ProductResponse update(Long id, UpdateProductRequest request) {
        ProductEntity product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));

        product.update(
                request.name(),
                request.description(),
                request.price(),
                request.stock()
        );

        return toResponse(product);
    }

    @Transactional
    public ProductResponse deactivate(Long id) {
        ProductEntity product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));

        product.deactivate();

        return toResponse(product);
    }

    @Transactional
    public ProductResponse activate(Long id) {
        ProductEntity product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));

        product.activate();

        return toResponse(product);
    }

    private ProductResponse toResponse(ProductEntity product) {
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getStock(),
                product.getActive(),
                product.getCreatedAt()
        );
    }
}