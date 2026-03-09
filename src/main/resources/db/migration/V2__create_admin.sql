INSERT INTO users (username, password, role)
    SELECT 'admin1', '$2a$12$nuB8N42DoxrSnX0SPYHfteoLvDAdt9CO48e0fdEzVeUbxVIMtfGjS', 'ADMIN'
    WHERE NOT EXISTS (
    SELECT 1 FROM users WHERE username = 'admin1'
);