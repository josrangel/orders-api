package com.jrangel.ordersapi.service;

import com.jrangel.ordersapi.dto.AuthResponse;
import com.jrangel.ordersapi.dto.LoginRequest;
import com.jrangel.ordersapi.dto.RegisterRequest;
import com.jrangel.ordersapi.entity.AuthUserEntity;
import com.jrangel.ordersapi.enums.AuthRole;
import com.jrangel.ordersapi.exception.AuthUserAlreadyExistsException;
import com.jrangel.ordersapi.exception.InvalidCredentialsException;
import com.jrangel.ordersapi.repository.AuthUserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private AuthUserRepository authUserRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthService authService;

    @Test
    void register_whenEmailDoesNotExist_shouldCreateUserAndReturnToken() {
        RegisterRequest request = new RegisterRequest(
                "user@example.com",
                "password123"
        );

        AuthUserEntity savedUser = new AuthUserEntity(
                request.email(),
                "encoded-password",
                AuthRole.USER
        );
        savedUser.setId(1L);

        when(authUserRepository.existsByEmail(request.email()))
                .thenReturn(false);

        when(passwordEncoder.encode(request.password()))
                .thenReturn("encoded-password");

        when(authUserRepository.save(any(AuthUserEntity.class)))
                .thenReturn(savedUser);

        when(jwtService.generateToken(savedUser))
                .thenReturn("fake-jwt-token");

        AuthResponse response = authService.register(request);

        assertNotNull(response);
        assertEquals("fake-jwt-token", response.token());
        assertEquals("Bearer", response.tokenType());
        assertEquals(1L, response.userId());
        assertEquals("user@example.com", response.email());
        assertEquals("USER", response.role());

        verify(authUserRepository, times(1)).existsByEmail(request.email());
        verify(passwordEncoder, times(1)).encode(request.password());
        verify(authUserRepository, times(1)).save(any(AuthUserEntity.class));
        verify(jwtService, times(1)).generateToken(savedUser);
    }

    @Test
    void register_whenEmailAlreadyExists_shouldThrowException() {
        RegisterRequest request = new RegisterRequest(
                "user@example.com",
                "password123"
        );

        when(authUserRepository.existsByEmail(request.email()))
                .thenReturn(true);

        AuthUserAlreadyExistsException exception = assertThrows(
                AuthUserAlreadyExistsException.class,
                () -> authService.register(request)
        );

        assertEquals(
                "Ya existe un usuario de autenticación con el email: user@example.com",
                exception.getMessage()
        );

        verify(authUserRepository, times(1)).existsByEmail(request.email());
        verifyNoInteractions(passwordEncoder);
        verify(authUserRepository, never()).save(any(AuthUserEntity.class));
        verifyNoInteractions(jwtService);
    }

    @Test
    void registerAdmin_whenEmailDoesNotExist_shouldCreateAdminAndReturnToken() {
        RegisterRequest request = new RegisterRequest(
                "admin@example.com",
                "password123"
        );

        AuthUserEntity savedUser = new AuthUserEntity(
                request.email(),
                "encoded-password",
                AuthRole.ADMIN
        );
        savedUser.setId(2L);

        when(authUserRepository.existsByEmail(request.email()))
                .thenReturn(false);

        when(passwordEncoder.encode(request.password()))
                .thenReturn("encoded-password");

        when(authUserRepository.save(any(AuthUserEntity.class)))
                .thenReturn(savedUser);

        when(jwtService.generateToken(savedUser))
                .thenReturn("fake-admin-jwt-token");

        AuthResponse response = authService.registerAdmin(request);

        assertNotNull(response);
        assertEquals("fake-admin-jwt-token", response.token());
        assertEquals("Bearer", response.tokenType());
        assertEquals(2L, response.userId());
        assertEquals("admin@example.com", response.email());
        assertEquals("ADMIN", response.role());

        verify(authUserRepository, times(1)).existsByEmail(request.email());
        verify(passwordEncoder, times(1)).encode(request.password());
        verify(authUserRepository, times(1)).save(any(AuthUserEntity.class));
        verify(jwtService, times(1)).generateToken(savedUser);
    }

    @Test
    void login_whenCredentialsAreValid_shouldReturnToken() {
        LoginRequest request = new LoginRequest(
                "user@example.com",
                "password123"
        );

        AuthUserEntity user = new AuthUserEntity(
                request.email(),
                "encoded-password",
                AuthRole.USER
        );
        user.setId(1L);

        when(authUserRepository.findByEmail(request.email()))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches(request.password(), user.getPassword()))
                .thenReturn(true);

        when(jwtService.generateToken(user))
                .thenReturn("fake-jwt-token");

        AuthResponse response = authService.login(request);

        assertNotNull(response);
        assertEquals("fake-jwt-token", response.token());
        assertEquals("Bearer", response.tokenType());
        assertEquals(1L, response.userId());
        assertEquals("user@example.com", response.email());
        assertEquals("USER", response.role());

        verify(authUserRepository, times(1)).findByEmail(request.email());
        verify(passwordEncoder, times(1)).matches(request.password(), user.getPassword());
        verify(jwtService, times(1)).generateToken(user);
    }

    @Test
    void login_whenEmailDoesNotExist_shouldThrowInvalidCredentialsException() {
        LoginRequest request = new LoginRequest(
                "missing@example.com",
                "password123"
        );

        when(authUserRepository.findByEmail(request.email()))
                .thenReturn(Optional.empty());

        InvalidCredentialsException exception = assertThrows(
                InvalidCredentialsException.class,
                () -> authService.login(request)
        );

        assertEquals("Credenciales inválidas", exception.getMessage());

        verify(authUserRepository, times(1)).findByEmail(request.email());
        verifyNoInteractions(passwordEncoder);
        verifyNoInteractions(jwtService);
    }

    @Test
    void login_whenPasswordIsInvalid_shouldThrowInvalidCredentialsException() {
        LoginRequest request = new LoginRequest(
                "user@example.com",
                "wrong-password"
        );

        AuthUserEntity user = new AuthUserEntity(
                request.email(),
                "encoded-password",
                AuthRole.USER
        );
        user.setId(1L);

        when(authUserRepository.findByEmail(request.email()))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches(request.password(), user.getPassword()))
                .thenReturn(false);

        InvalidCredentialsException exception = assertThrows(
                InvalidCredentialsException.class,
                () -> authService.login(request)
        );

        assertEquals("Credenciales inválidas", exception.getMessage());

        verify(authUserRepository, times(1)).findByEmail(request.email());
        verify(passwordEncoder, times(1)).matches(request.password(), user.getPassword());
        verifyNoInteractions(jwtService);
    }
}