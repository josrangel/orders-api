INSERT INTO auth_users (email, password, role, enabled, created_at)
VALUES (
           'root@example.com',
           '$2a$10$7EqJtq98hPqEX7fNZaFWoOhiu81hB7WT6W7krKkXbzM2pX6S.3TXG',
           'ADMIN',
           TRUE,
           CURRENT_TIMESTAMP
       )
    ON CONFLICT (email) DO NOTHING;