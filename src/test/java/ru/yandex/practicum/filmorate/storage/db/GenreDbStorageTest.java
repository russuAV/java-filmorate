package ru.yandex.practicum.filmorate.storage.db;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.filmdata.Genre;
import ru.yandex.practicum.filmorate.model.filmdata.MpaRating;
import ru.yandex.practicum.filmorate.storage.mapper.FilmRowMapper;
import ru.yandex.practicum.filmorate.storage.mapper.GenreRowMapper;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@Import({GenreDbStorage.class, GenreRowMapper.class, FilmDbStorage.class, FilmRowMapper.class})
class GenreDbStorageTest {

    @Autowired
    private GenreDbStorage genreDbStorage;

    @Autowired
    private JdbcTemplate jdbc;

    @Autowired
    private FilmDbStorage filmDbStorage;


    @Test
    void findAllGenres() {
        List<Genre> genres = genreDbStorage.findAll();

        assertThat(genres).isNotEmpty();
        assertThat(genres)
                .extracting(Genre::getName)
                .contains("Комедия", "Драма", "Триллер", "Мультфильм", "Триллер", "Боевик");
    }

    @Test
    void getGenreById() {
        Optional<Genre> genreOpt = genreDbStorage.getById(1L);

        assertThat(genreOpt).isPresent();
        assertThat(genreOpt.get().getId()).isEqualTo(1L);
        assertThat(genreOpt.get().getName()).isEqualTo("Комедия");
    }

    @Test
    void getGenreByInvalidIdReturnsEmpty() {
        Optional<Genre> genre = genreDbStorage.getById(999L);

        assertThat(genre).isEmpty();
    }

    @Test
    void getGenresByFilmId() {
        jdbc.update("DELETE FROM film_genres");
        jdbc.update("DELETE FROM films");

        Film film = filmDbStorage.create(new Film(null, LocalDate.of(1999, 12, 12),
                "Film One", "Desc One", 90,
                List.of(new Genre(5L, null), new Genre(6L, null)),
                new MpaRating(1L, "G")));

        List<Genre> genres = genreDbStorage.getGenresByFilmId(film.getId());

        assertThat(genres).hasSize(2);
        assertThat(genres)
                .extracting(Genre::getId)
                .containsExactlyInAnyOrder(5L, 6L);
        assertThat(genres)
                .extracting(Genre::getName)
                .containsExactlyInAnyOrder("Документальный", "Боевик");
    }
}