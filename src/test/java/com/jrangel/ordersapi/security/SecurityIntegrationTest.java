package com.jrangel.ordersapi.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jrangel.ordersapi.entity.AuthUserEntity;
import com.jrangel.ordersapi.enums.AuthRole;
import com.jrangel.ordersapi.repository.AuthUserRepository;
import com.jrangel.ordersapi.service.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class SecurityIntegrationTest {

    @Container
    static PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:16")
            .withDatabaseName("orders_security_test_db")
            .withUsername("test_user")
            .withPassword("test_password");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);

        registry.add("spring.flyway.enabled", () -> true);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "validate");

        registry.add(
                "app.security.jwt.secret",
                () -> "1234567890123456789012345678901234567890123456789012345678901234"
        );
        registry.add("app.security.jwt.expiration-minutes", () -> "60");
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AuthUserRepository authUserRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getProducts_withoutToken_shouldReturn401() throws Exception {
        mockMvc.perform(get("/api/products"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void createProduct_withUserRole_shouldReturn403() throws Exception {
        AuthUserEntity user = createAuthUser(
                "user.security@example.com",
                AuthRole.USER
        );

        String token = jwtService.generateToken(user);

        String body = """
                {
                  "name": "Producto USER",
                  "description": "Este producto no debería crearse",
                  "price": 100.00,
                  "stock": 5
                }
                """;

        mockMvc.perform(post("/api/products")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isForbidden());
    }

    @Test
    void createProduct_withAdminRole_shouldReturn201() throws Exception {
        AuthUserEntity admin = createAuthUser(
                "admin.security@example.com",
                AuthRole.ADMIN
        );

        String token = jwtService.generateToken(admin);

        String body = """
                {
                  "name": "Producto ADMIN",
                  "description": "Producto creado por admin",
                  "price": 250.00,
                  "stock": 10
                }
                """;

        mockMvc.perform(post("/api/products")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated());
    }

    private AuthUserEntity createAuthUser(String email, AuthRole role) {
        AuthUserEntity user = new AuthUserEntity(
                email,
                passwordEncoder.encode("password123"),
                role
        );

        return authUserRepository.save(user);
    }
}