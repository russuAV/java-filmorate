-- Жанры
--MERGE INTO genres (name) VALUES
--  ('Комедия'),
--  ('Драма'),
--  ('Мультфильм'),
--  ('Триллер'),
--  ('Документальный'),
--  ('Боевик');
MERGE INTO genres (name) KEY(name) VALUES
  ('Комедия'),
  ('Драма'),
  ('Мультфильм'),
  ('Триллер'),
  ('Документальный'),
  ('Боевик');
---- MPA рейтинги
--MERGE INTO mpa_ratings (name) VALUES
--  ('G'),
--  ('PG'),
--  ('PG-13'),
--  ('R'),
--  ('NC-17');
MERGE INTO mpa_ratings (name) KEY(name) VALUES
  ('G'),
  ('PG'),
  ('PG-13'),
  ('R'),
  ('NC-17');
-- Пользователи
INSERT INTO users (email, login, name, birthday) VALUES
  ('roma@example.com', 'roma', 'Roma', '1990-01-01'),
  ('vladimir@example.com', 'vladimir', 'Vladimir', '1992-05-10'),
  ('aleksandr@example.com', 'aleksandr', 'Aleksandr', '1988-12-15');

-- Фильмы
INSERT INTO films (name, description, release_date, duration, mpa_rating_id) VALUES
  ('Inception', 'A mind-bending thriller', '2010-07-16', 148, 3),
  ('Titanic', 'Epic romance and disaster', '1997-12-19', 195, 2);

-- Жанры фильмов
INSERT INTO film_genres (film_id, genre_id) VALUES
  (1, 4),  -- Inception
  (1, 2),  -- Inception
  (2, 5);  -- Titanic

--  Лайки
INSERT INTO likes (film_id, user_id) VALUES
  (1, 1),
  (1, 2),
  (2, 3);

-- Дружба
INSERT INTO friendships (sender_id, receiver_id, status) VALUES
  (1, 2, 'pending');