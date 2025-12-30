-- ===== USERS =====
-- Obavezna polja po tvojoj User klasi:
-- email, username, password, first_name, last_name, address, enabled, created_at
-- activation_token je optional

INSERT INTO users (id, email, username, password, first_name, last_name, address, enabled, activation_token, created_at)
VALUES
    (1, 'pera@example.com', 'pera', '$2a$10$testhashpera', 'Pera', 'Peric', 'Bulevar 1, Novi Sad', TRUE,  NULL, NOW()),
    (2, 'mika@example.com', 'mika', '$2a$10$testhashmika', 'Mika', 'Mikic', 'Ulica 2, Beograd',   TRUE,  NULL, NOW()),
    (3, 'ana@example.com',  'ana',  '$2a$10$testhashana',  'Ana',  'Anic',  'Ulica 3, Nis',        FALSE, 'ACT-123', NOW());

-- ===== VIDEOS =====
-- created_at MORA da se upise jer SQL preskace @PrePersist
-- user_id mora da postoji u users tabeli

INSERT INTO video (title, description, created_at, user_id)
VALUES
    ('Video 1', 'Opis prvog videa', NOW() - INTERVAL '2 days', 1),
    ('Video 2', 'Opis drugog videa', NOW() - INTERVAL '1 day', 2),
    ('Spring Boot Test', 'Video ubačen preko import.sql', NOW(), 1);
