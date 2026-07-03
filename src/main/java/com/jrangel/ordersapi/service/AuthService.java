package com.jrangel.ordersapi.service;

import com.jrangel.ordersapi.dto.AuthResponse;
import com.jrangel.ordersapi.dto.LoginRequest;
import com.jrangel.ordersapi.dto.RegisterRequest;
import com.jrangel.ordersapi.entity.AuthUserEntity;
import com.jrangel.ordersapi.enums.AuthRole;
import com.jrangel.ordersapi.exception.AuthUserAlreadyExistsException;
import com.jrangel.ordersapi.exception.InvalidCredentialsException;
import com.jrangel.ordersapi.repository.AuthUserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final AuthUserRepository authUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(
            AuthUserRepository authUserRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService
    ) {
        this.authUserRepository = authUserRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (authUserRepository.existsByEmail(request.email())) {
            throw new AuthUserAlreadyExistsException(request.email());
        }

        AuthUserEntity user = new AuthUserEntity(
                request.email(),
                passwordEncoder.encode(request.password()),
                AuthRole.USER
        );

        AuthUserEntity savedUser = authUserRepository.save(user);

        String token = jwtService.generateToken(savedUser);

        return toResponse(savedUser, token);
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        AuthUserEntity user = authUserRepository.findByEmail(request.email())
                .orElseThrow(InvalidCredentialsException::new);

        if (!Boolean.TRUE.equals(user.getEnabled())) {
            throw new InvalidCredentialsException();
        }

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new InvalidCredentialsException();
        }

        String token = jwtService.generateToken(user);

        return toResponse(user, token);
    }

    @Transactional
    public AuthResponse registerAdmin(RegisterRequest request) {
        if (authUserRepository.existsByEmail(request.email())) {
            throw new AuthUserAlreadyExistsException(request.email());
        }

        AuthUserEntity user = new AuthUserEntity(
                request.email(),
                passwordEncoder.encode(request.password()),
                AuthRole.ADMIN
        );

        AuthUserEntity savedUser = authUserRepository.save(user);

        String token = jwtService.generateToken(savedUser);

        return toResponse(savedUser, token);
    }

    private AuthResponse toResponse(AuthUserEntity user, String token) {
        return new AuthResponse(
                token,
                "Bearer",
                user.getId(),
                user.getEmail(),
                user.getRole().name()
        );
    }
}