# java-filmorate
Filmorate — это приложение для управления информацией о фильмах, пользователях, лайках и дружбе.
На текущем этапе реализована схема базы данных и модель в коде.

[Схема базы данных](https://dbdiagram.io/d/67e2754c75d75cc8445dd620)

Пример SQL запроса для получения всех фильмов с жанрами и рейтингом:

```sql
SELECT f.*,
       g.name AS genre,
       m.name AS rating
FROM films AS f
LEFT JOIN film_genres AS fg ON f.id = fg.film_id
LEFT JOIN genres AS s g ON fg.genre_id = g.id
LEFT JOIN mpa_ratings AS m ON f.mpa_rating_id = m.id;
```

Пример SQL запроса на получение фильмов по количеству лайков:

```sql
SELECT f.id,
       f.name,
       COUNT(l.user_id) AS likes_count
FROM films AS f
LEFT JOIN likes AS l ON f.id = l.film_id
GROUP BY f.id,
         f.name
ORDER BY likes_count DESC
```

Запрос на получение пользователя с id = 1:

``` sql
SELECT *
FROM users
WHERE id = 1;
```

Запрос на получение фильма с id = 1 с информацией о жанрах и рейтинге:

```sql
SELECT f.*,
       g.name AS genre,
       m.name AS rating
FROM films AS f
LEFT JOIN film_genres AS fg ON f.id = fg.film_id
LEFT JOIN genres AS g ON g.id = fg.genre_id
LEFT JOIN mpa_ratings AS m ON f.mpa_rating_id = m.id
WHERE f.id = 1;
```

Запрос на получение общих друзей между пользователем id=1 и пользователем id=2:
- Для начала в первом запросе получаем список друзей для user_id = 1 со статусом "confirmed"
- Во втором запросе получаем список друзей для user_id = 2 со статусом "confirmed"
- С помощью оператора INTERSECT возвращаем только те id, которые встречаются в первом и втором запросах

```sql
(
  (SELECT friend_id AS friend_id
   FROM friendships
   WHERE user_id = 1 AND status = 'confirmed')
  UNION
  (SELECT user_id AS friend_id
   FROM friendships
   WHERE friend_id = 1 AND status = 'confirmed')
)
INTERSECT
(
  (SELECT friend_id AS friend_id
   FROM friendships
   WHERE user_id = 2 AND status = 'confirmed')
  UNION
  (SELECT user_id AS friend_id
   FROM friendships
   WHERE friend_id = 2 AND status = 'confirmed')
);
```