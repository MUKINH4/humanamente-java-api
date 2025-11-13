-- Inserir usuário ADMIN
-- Username: admin
-- Email: admin@humanamente.com
-- Password: admin123 (BCrypt hash)
INSERT INTO users (id, username, email, password, role) 
VALUES (
    'a1b2c3d4-e5f6-7890-abcd-ef1234567890',
    'admin',
    'admin@humanamente.com',
    '$2a$10$O5RzMSeH0d51HbstYgWnde3pIqxaCK3qY1kJzGfVwih3p6wn63cZq',
    'ADMIN'
);

-- Inserir usuário COMUM
-- Username: user
-- Email: user@humanamente.com
-- Password: user123 (BCrypt hash)
INSERT INTO users (id, username, email, password, role) 
VALUES (
    'b2c3d4e5-f6a7-8901-bcde-f12345678901',
    'user',
    'user@humanamente.com',
    '$2a$10$YH7Kin4SGl1G/h0PGCxPG.rhZSgsuizRNkFLXLhXWJQgxbSGChVVC',
    'USER'
);

-- Comentários
COMMENT ON TABLE users IS 'Usuários cadastrados: admin (admin@humanamente.com / admin123) e user (user@humanamente.com / user123)';
