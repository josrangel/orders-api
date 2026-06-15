package com.jrangel.ordersapi.service;

import com.jrangel.ordersapi.dto.CreateUserRequest;
import com.jrangel.ordersapi.dto.UserResponse;
import com.jrangel.ordersapi.entity.UserEntity;
import com.jrangel.ordersapi.exception.EmailAlreadyExistsException;
import com.jrangel.ordersapi.exception.UserNotFoundException;
import com.jrangel.ordersapi.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public UserResponse create(CreateUserRequest request) {
        userRepository.findByEmail(request.email())
                .ifPresent(user -> {
                    throw new EmailAlreadyExistsException(request.email());
                });

        UserEntity user = new UserEntity(
                request.name(),
                request.email()
        );

        UserEntity savedUser = userRepository.save(user);

        return toResponse(savedUser);
    }

    @Transactional(readOnly = true)
    public UserResponse findById(Long id) {
        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        return toResponse(user);
    }

    @Transactional(readOnly = true)
    public List<UserResponse> findAll() {
        return userRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private UserResponse toResponse(UserEntity user) {
        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getCreatedAt()
        );
    }
}