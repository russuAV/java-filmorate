# FilmoRate
[![Java](https://img.shields.io/badge/Java-21-blue)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-green)](https://spring.io/projects/spring-boot)
[![Build](https://img.shields.io/badge/Maven-success)](https://maven.apache.org/)
[![Tests](https://img.shields.io/badge/JUnit5-brightgreen)](https://junit.org/junit5/)

FilmoRate — мини‑соцсеть для любителей кино. Пользователи добавляют фильмы, ставят лайки, формируют топы, дружат между собой и смотрят общих друзей. Проект демонстрирует слоистую архитектуру (Controller → Service → Storage/Repository), валидацию входных данных и централизованную обработку ошибок.

## Возможности
- **Управление пользователями:** Регистрация, обновление, получение по ID.
- **Дружба:** Добавление/удаление друзей, получение списков друзей и общих друзей.
- **Управление фильмами:** Добавление, обновление, получение и поиск фильмов.
- **Лайки:** Постановка и удаление лайков, получение списка популярных фильмов.
- **Валидация и обработка ошибок:** Проверка входных данных и централизованная обработка исключений.
- **Логирование:** Отслеживание операций приложения.
- **Тестирование:** Набор юнит-тестов, обеспечивающих стабильность проекта.

## Используемые Технологии

- **Java 21**
- **Spring Boot:** Spring Web, Spring Validation.
- **Lombok**
- **JUnit 5**
- **Mockito**
- **JSON**
- **Logback**
- **Maven**

## Настройка

1. Склонируйте репозиторий:
    ```bash
    git clone https://github.com/russuAV/java-filmorate.git
    ```
2. Перейдите в директорию проекта:
    ```bash
    cd filmorate
    ```
3. Настройте параметры подключения к базе данных в файле `src/main/resources/application.properties`. Пример для PostgreSQL:
    ```properties
    spring.datasource.url=jdbc:postgresql://localhost:5432/filmorate_db
    spring.datasource.username=your_username
    spring.datasource.password=your_password
    spring.jpa.hibernate.ddl-auto=update
    ```
   > Для тестирования можно использовать встроенную базу данных H2.
4. Соберите проект с помощью Maven:
    ```bash
    mvn clean install
    ```


## Запуск приложения

Запустите приложение следующей командой:
```bash
  mvn spring-boot:run
```
После запуска, приложение будет доступно по адресу: [http://localhost:8080](http://localhost:8080).

## API Endpoints


<details>
  <summary><b>Пользователи</b></summary>


- `GET /users` — Получить всех пользователей.
- `POST /users` — Создать нового пользователя.
- `PUT /users` — Обновить существующего пользователя.
- `GET /users/{id}` — Получить пользователя по ID.
- `PUT /users/{id}/friends/{friendId}` — Добавить друга.
- `DELETE /users/{id}/friends/{friendId}` — Удалить друга.
- `GET /users/{id}/friends` — Получить список друзей.
- `GET /users/{id}/friends/common/{otherId}` — Получить общих друзей.
</details>

<details>
  <summary><b>Фильмы</b></summary>

- `GET /films` — Получить список всех фильмов.
- `POST /films` — Добавить новый фильм.
- `PUT /films` — Обновить фильм.
- `GET /films/{id}` — Получить фильм по ID.
- `PUT /films/{id}/like/{userId}` — Добавить лайк фильму.
- `DELETE /films/{id}/like/{userId}` — Удалить лайк с фильма.
- `GET /films/popular?count={count}` — Получить наиболее популярные фильмы.
</details>

[Схема базы данных](https://dbdiagram.io/d/67e2754c75d75cc8445dd620)

## SQL Примеры

#### Пример SQL запроса для получения всех фильмов с жанрами и рейтингом:

```sql
SELECT f.*,
       g.name AS genre,
       m.name AS rating
FROM films AS f
LEFT JOIN film_genres AS fg ON f.id = fg.film_id
LEFT JOIN genres AS g ON fg.genre_id = g.id
LEFT JOIN mpa_ratings AS m ON f.mpa_rating_id = m.id;
```

#### Пример SQL запроса на получение фильмов по количеству лайков:

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

#### Запрос на получение пользователя с id = 1:

``` sql
SELECT *
FROM users
WHERE id = 1;
```

#### Запрос на получение фильма с id = 1 с информацией о жанрах и рейтинге:

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

#### Запрос на получение общих друзей между пользователем id=1 и пользователем id=2:
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

## Тестирование

Для запуска тестов выполните:
```
mvn test
```