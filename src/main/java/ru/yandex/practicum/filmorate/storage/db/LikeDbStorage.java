package ru.yandex.practicum.filmorate.storage.db;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.LikeStorage;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class LikeDbStorage implements LikeStorage {
    private final JdbcTemplate jdbc;
    private final RowMapper<Film> mapper;

    private static final String ADD_LIKE_QUERY = """
            INSERT INTO likes(film_id, user_id)
            VALUES (?, ?)
            """;
    private static final String DELETE_LIKE_QUERY = """
            DELETE FROM likes
            WHERE film_id = ?
            AND user_id = ?
            """;
    private static final String GET_LIKES_QUERY = """
            SELECT user_id
            FROM likes
            WHERE film_id = ?
            """;
    private static final String EXIST_LIKE_QUERY = """
            SELECT EXISTS (
                SELECT 1
                FROM likes
                WHERE film_id = ? AND user_id = ?
            )
            """;
    private static final String GET_TOP_FILM_IDS_QUERY = """
            SELECT film_id
            FROM likes
            GROUP BY film_id
            ORDER BY COUNT(user_id) DESC
            LIMIT ?
            """;

    @Override
    public void addLike(Long filmId, Long userId) {
        jdbc.update(ADD_LIKE_QUERY, filmId, userId);
    }

    @Override
    public void removeLike(Long filmId, Long userId) {
        jdbc.update(DELETE_LIKE_QUERY, filmId, userId);
    }

    @Override
    public List<Long> getUserIdsWhoLikedFilm(Long filmId) {
        return jdbc.queryForList(GET_LIKES_QUERY, Long.class, filmId);
    }

    @Override
    public List<Long> getTopFilmIds(int count) {
        return jdbc.queryForList(GET_TOP_FILM_IDS_QUERY, Long.class, count);
    }

    @Override
    public boolean existLike(Long filmId, Long userId) {
        Boolean result = jdbc.queryForObject(EXIST_LIKE_QUERY, Boolean.class, filmId, userId);
        return Boolean.TRUE.equals(result);
    }
}