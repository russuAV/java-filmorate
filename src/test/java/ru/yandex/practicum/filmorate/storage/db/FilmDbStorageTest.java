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
@Import({FilmDbStorage.class, FilmRowMapper.class, GenreRowMapper.class})
class FilmDbStorageTest {

    @Autowired
    private FilmDbStorage filmDbStorage;

    @Autowired
    private JdbcTemplate jdbc;

    @Test
    public void findAll() {
        jdbc.update("DELETE FROM films");

        filmDbStorage.create(new Film(null, LocalDate.of(1999, 12, 12),
                "Film One", "Desc One", 90,
                List.of(new Genre(1L, "Comedy")), new MpaRating(1L, "G")));

        filmDbStorage.create(new Film(null, LocalDate.of(2005, 6, 6),
                "Film Two", "Desc Two", 110,
                List.of(new Genre(2L, "Action")), new MpaRating(2L, "PG")));

        List<Film> films = filmDbStorage.findAll();
        assertThat(films).hasSize(2);
    }

    @Test
    void testCreateAndFindFilmById() {
        Film film = new Film(null, LocalDate.of(2000, 1, 1),
                "Ship", "A ship sinks", 180,
                List.of(new Genre(1L, "Drama")), new MpaRating(1L, "G"));

        Film created = filmDbStorage.create(film);
        Optional<Film> found = filmDbStorage.getFilmById(created.getId());
        System.out.println(found);

        assertThat(found)
                .isPresent()
                .hasValueSatisfying(f -> {
                    assertThat(f.getName()).isEqualTo("Ship");
                    assertThat(f.getDuration()).isEqualTo(180);
                    assertThat(f.getReleaseDate()).isEqualTo(LocalDate.of(2000,1,1));
                    assertThat(f.getMpaRating().getId()).isEqualTo(1);
                });
    }

    @Test
    void updateFilm() {
        Film film = new Film(null, LocalDate.of(2001, 1, 1),
                "Old Title", "Old desc", 120,
                List.of(new Genre(1L, "Drama")), new MpaRating(1L, "G"));

        Film created = filmDbStorage.create(film);
        created.setName("New Title");
        created.setDescription("New desc");
        created.setDuration(150);

        Film updated = filmDbStorage.update(created);

        assertThat(updated.getName()).isEqualTo("New Title");
        assertThat(updated.getDescription()).isEqualTo("New desc");
        assertThat(updated.getDuration()).isEqualTo(150);
    }

    @Test
    void testDeleteFilm() {
        Film film = new Film(null, LocalDate.of(1999, 12, 12),
                "To Delete", "Should be gone", 90,
                List.of(new Genre(1L, "Comedy")), new MpaRating(1L, "G"));

        Film created = filmDbStorage.create(film);
        filmDbStorage.delete(created.getId());

        Optional<Film> deleted = filmDbStorage.getFilmById(created.getId());
        assertThat(deleted).isEmpty();
    }
}