package com.jrangel.ordersapi.repository;

import com.jrangel.ordersapi.entity.OrderEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<OrderEntity, Long> {

    Page<OrderEntity> findByUserId(Long userId, Pageable pageable);
}