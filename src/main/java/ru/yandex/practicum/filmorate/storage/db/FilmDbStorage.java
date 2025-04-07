package ru.yandex.practicum.filmorate.storage.db;

import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.filmdata.Genre;
import ru.yandex.practicum.filmorate.storage.BaseBdStorage;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@Primary
public class FilmDbStorage extends BaseBdStorage<Film> implements FilmStorage {
    private static final String FIND_ALL_QUERY = """
            SELECT f.*
            FROM films f
            JOIN film_genres fg ON fg.film_id = f.id
            JOIN genres g ON g.id = fg.genre_id
            GROUP BY f.id
            """;
    private static final String FIND_BY_ID_QUERY = """
            SELECT *
            FROM films
            WHERE id = ?
            """;
    private static final String FIND_BY_GENRE_QUERY = """
            SELECT *
            FROM films f
            JOIN film_genres fg ON f.id = fg.film_id
            JOIN genres g ON g.id = fg.genre_id
            WHERE fg.genre_id = ?
            """;
    private static final String FIND_BY_GENRES_QUERY = """
            SELECT DISTINCT f.*
            FROM films f
            JOIN film_genres fg ON fg.film_id = f.id
            WHERE fg.genre_id IN (%s)
            """;

    private static final String FIND_BY_MPA_QUERY = """
            SELECT *
            FROM films f
            WHERE mpa_rating_id = ?
            """;
    private static final String INSERT_QUERY = """
            INSERT INTO films (name, description, release_date, duration, mpa_rating_id)
            VALUES (?, ?, ?, ?, ?)
            """;
    private static final String INSERT_FILM_GENRE_QUERY = """
            INSERT INTO film_genres (film_id, genre_id)
            VALUES (?, ?)
            """;
    private static final String UPDATE_QUERY = """
            UPDATE films SET name = ?, description = ?, release_date = ?, duration = ?, mpa_rating_id = ?
            WHERE id = ?
            """;

    private static final String DELETE_FILM_QUERY = """
            DELETE FROM films
            WHERE id = ?
            """;
    private static final String DELETE_FILM_LIKES_QUERY = """
            DELETE FROM likes
            WHERE film_id = ?
            """;
    private static final String DELETE_FILM_GENRES_QUERY = """
            DELETE FROM film_genres
            WHERE film_id = ?
            """;

    public FilmDbStorage(JdbcTemplate jdbc, RowMapper<Film> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public List<Film> findAll() {
        return findMany(FIND_ALL_QUERY);
    }

    @Override
    public Film update(Film film) {
        update(UPDATE_QUERY,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpaRating().getId(),
                film.getId()
        );
        return film;
    }

    @Override
    public Film create(Film film) {
        long id = insert(
                INSERT_QUERY,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpaRating().getId()
        );
        film.setId(id);
        insertGenres(id, film.getGenres());

        return film;
    }

    @Override
    public void delete(Long filmId) {
        jdbc.update(DELETE_FILM_LIKES_QUERY, filmId);
        jdbc.update(DELETE_FILM_GENRES_QUERY, filmId);
        jdbc.update(DELETE_FILM_QUERY, filmId);
    }

    @Override
    public Optional<Film> getFilmById(Long filmId) {
        return findOne(FIND_BY_ID_QUERY, filmId);
    }

    @Override
    public List<Film> getFilmsByGenreId(Long genreId) {
        return findMany(FIND_BY_GENRE_QUERY, genreId);
    }

    @Override
    public List<Film> getFilmsByGenreIds(List<Long> genreIds) {
        String inSql = genreIds.stream()
                .map(id -> "?")
                .collect(Collectors.joining(", "));
        String query = String.format(FIND_BY_GENRES_QUERY, inSql);
        return jdbc.query(query, mapper, genreIds.toArray());
    }

    @Override
    public List<Film> getFilmsByMpaRatingId(Long mpaRatingId) {
        return findMany(FIND_BY_MPA_QUERY, mpaRatingId);
    }

    public void insertGenres(Long filmId, List<Genre> genres) {
        for (Genre genre : genres) {
            jdbc.update(INSERT_FILM_GENRE_QUERY, filmId, genre.getId());
        }
    }
}