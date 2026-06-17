package com.jrangel.ordersapi.service;

import com.jrangel.ordersapi.dto.CreateOrderItemRequest;
import com.jrangel.ordersapi.dto.CreateOrderRequest;
import com.jrangel.ordersapi.dto.OrderItemResponse;
import com.jrangel.ordersapi.dto.OrderResponse;
import com.jrangel.ordersapi.entity.OrderEntity;
import com.jrangel.ordersapi.entity.OrderItemEntity;
import com.jrangel.ordersapi.entity.ProductEntity;
import com.jrangel.ordersapi.entity.UserEntity;
import com.jrangel.ordersapi.exception.*;
import com.jrangel.ordersapi.repository.OrderRepository;
import com.jrangel.ordersapi.repository.ProductRepository;
import com.jrangel.ordersapi.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    public OrderService(
            OrderRepository orderRepository,
            UserRepository userRepository,
            ProductRepository productRepository
    ) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
    }

    @Transactional
    public OrderResponse create(CreateOrderRequest request) {
        UserEntity user = userRepository.findById(request.userId())
                .orElseThrow(() -> new UserNotFoundException(request.userId()));

        Map<Long, Integer> requestedQuantityByProductId = request.items()
                .stream()
                .collect(Collectors.groupingBy(
                        CreateOrderItemRequest::productId,
                        Collectors.summingInt(CreateOrderItemRequest::quantity)
                ));

        List<ProductEntity> products = productRepository.findAllById(
                requestedQuantityByProductId.keySet()
        );

        validateAllProductsExist(requestedQuantityByProductId, products);

        Map<Long, ProductEntity> productById = products.stream()
                .collect(Collectors.toMap(
                        ProductEntity::getId,
                        Function.identity()
                ));

        validateProductsCanBeSold(requestedQuantityByProductId, productById);

        OrderEntity order = new OrderEntity(user);

        for (CreateOrderItemRequest itemRequest : request.items()) {
            ProductEntity product = productById.get(itemRequest.productId());

            OrderItemEntity item = new OrderItemEntity(product, itemRequest.quantity());
            order.addItem(item);
        }

        requestedQuantityByProductId.forEach((productId, quantity) -> {
            ProductEntity product = productById.get(productId);
            product.decreaseStock(quantity);
        });

        order.calculateTotal();

        OrderEntity savedOrder = orderRepository.save(order);

        return toResponse(savedOrder);
    }

    @Transactional(readOnly = true)
    public OrderResponse findById(Long id) {
        OrderEntity order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException(id));

        return toResponse(order);
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> findByUserId(Long userId) {
        return orderRepository.findByUserId(userId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private OrderResponse toResponse(OrderEntity order) {
        List<OrderItemResponse> items = order.getItems()
                .stream()
                .map(item -> new OrderItemResponse(
                        item.getId(),
                        item.getProduct().getId(),
                        item.getProduct().getName(),
                        item.getQuantity(),
                        item.getUnitPrice(),
                        item.getSubtotal()
                ))
                .toList();

        return new OrderResponse(
                order.getId(),
                order.getUser().getId(),
                order.getUser().getName(),
                order.getStatus(),
                order.getTotal(),
                order.getCreatedAt(),
                items
        );
    }

    private void validateAllProductsExist(
            Map<Long, Integer> requestedQuantityByProductId,
            List<ProductEntity> products
    ) {
        if (products.size() != requestedQuantityByProductId.size()) {
            Map<Long, ProductEntity> productById = products.stream()
                    .collect(Collectors.toMap(
                            ProductEntity::getId,
                            Function.identity()
                    ));

            Long missingProductId = requestedQuantityByProductId.keySet()
                    .stream()
                    .filter(productId -> !productById.containsKey(productId))
                    .findFirst()
                    .orElseThrow();

            throw new ProductNotFoundException(missingProductId);
        }
    }

    private void validateProductsCanBeSold(
            Map<Long, Integer> requestedQuantityByProductId,
            Map<Long, ProductEntity> productById
    ) {
        requestedQuantityByProductId.forEach((productId, requestedQuantity) -> {
            ProductEntity product = productById.get(productId);

            validateProductCanBeSold(product, requestedQuantity);
        });
    }

    private void validateProductCanBeSold(ProductEntity product, Integer requestedQuantity) {
        if (!Boolean.TRUE.equals(product.getActive())) {
            throw new InactiveProductException(product.getId());
        }

        if (product.getStock() < requestedQuantity) {
            throw new InsufficientStockException(
                    product.getId(),
                    product.getStock(),
                    requestedQuantity
            );
        }
    }
}