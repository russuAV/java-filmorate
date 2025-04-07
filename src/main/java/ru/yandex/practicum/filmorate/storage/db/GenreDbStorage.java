package ru.yandex.practicum.filmorate.storage.db;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.filmdata.Genre;
import ru.yandex.practicum.filmorate.storage.BaseBdStorage;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.util.List;
import java.util.Optional;

@Repository
public class GenreDbStorage extends BaseBdStorage<Genre> implements GenreStorage {

    private static final String FIND_ALL_QUERY = """
            SELECT *
            FROM genres
            """;
    private static final String FIND_BY_ID_QUERY = """
            SELECT *
            FROM genres
            WHERE id = ?
            """;
    private static final String FIND_GENRES_BY_FILM_ID_QUERY = """
            SELECT g.id, g.name
            FROM film_genres fg
            JOIN genres g ON fg.genre_id = g.id
            WHERE fg.film_id = ?
            """;

    public GenreDbStorage(JdbcTemplate jdbc, RowMapper<Genre> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public Optional<Genre> getById(long genreId) {
            return findOne(FIND_BY_ID_QUERY, genreId);
    }

    @Override
    public List<Genre> findAll() {
        return findMany(FIND_ALL_QUERY);
    }

    @Override
    public List<Genre> getGenresByFilmId(Long filmId) {
        return findMany(FIND_GENRES_BY_FILM_ID_QUERY, filmId);
    }
}