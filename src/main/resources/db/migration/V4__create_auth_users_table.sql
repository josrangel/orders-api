CREATE TABLE auth_users (
                            id BIGSERIAL PRIMARY KEY,
                            email VARCHAR(150) NOT NULL UNIQUE,
                            password VARCHAR(255) NOT NULL,
                            role VARCHAR(50) NOT NULL,
                            enabled BOOLEAN NOT NULL DEFAULT TRUE,
                            created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);