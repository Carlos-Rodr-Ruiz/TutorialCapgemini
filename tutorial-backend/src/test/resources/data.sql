-- Limpieza
DELETE FROM loan;
DELETE FROM game;
DELETE FROM client;
DELETE FROM author;
DELETE FROM category;

-- Datos mínimos para tests de integración

INSERT INTO author (id, name) VALUES (1, 'Autor Test');
INSERT INTO category (id, name) VALUES (1, 'Categoría Test');
INSERT INTO game (id, title, age, author_id, category_id) VALUES (2, 'Juego Test', '18+', 1, 1);
INSERT INTO client (id, name) VALUES (1, 'Carlos Test');
