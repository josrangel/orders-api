package com.jrangel.ordersapi.repository;

import com.jrangel.ordersapi.entity.AuthUserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AuthUserRepository extends JpaRepository<AuthUserEntity, Long> {

    Optional<AuthUserEntity> findByEmail(String email);

    boolean existsByEmail(String email);
}