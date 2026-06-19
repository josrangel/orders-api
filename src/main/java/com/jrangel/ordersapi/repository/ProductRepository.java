package com.jrangel.ordersapi.repository;

import com.jrangel.ordersapi.entity.ProductEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<ProductEntity, Long> {

    Page<ProductEntity> findByActiveTrue(Pageable pageable);

    Page<ProductEntity> findByActiveTrueAndNameContainingIgnoreCase(
            String name,
            Pageable pageable
    );
}
