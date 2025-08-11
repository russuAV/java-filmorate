package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.dto.NewFilmRequest;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.filmdata.Genre;
import ru.yandex.practicum.filmorate.model.filmdata.MpaRating;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@AutoConfigureTestDatabase
class GenreServiceTest {

    @Autowired
    private GenreService genreService;
    @Autowired
    private FilmService filmService;


    @Test
    void shouldFindAllGenres() {
        List<Genre> genres = genreService.findAll();
        assertThat(genres).isNotEmpty();
        assertThat(genres).extracting(Genre::getName)
                .containsExactly("Комедия", "Драма", "Мультфильм", "Триллер", "Документальный", "Боевик");
    }

    @Test
    void shouldFindGenreById() {
        Genre genre = genreService.getGenreById(1L);
        assertThat(genre).isNotNull();
        assertThat(genre.getId()).isEqualTo(1L);
        assertThat(genre.getName()).isEqualTo("Комедия");
    }

    @Test
    void shouldThrowExceptionIfGenreNotFound() {
        assertThatThrownBy(() -> genreService.getGenreById(999L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Жанр с id " + 999L + " не найден.");
    }

    @Test
    void shouldReturnGenresByFilmId() {
        // Создаем фильм с жанрами
        NewFilmRequest request = new NewFilmRequest(
                "Test film",
                "desc",
                LocalDate.of(2000, 1, 1),
                100,
                new MpaRating(1L, null),
                List.of(new Genre(1L, null), new Genre(2L, null))
        );
        Film created = filmService.create(request);

        List<Genre> genres = genreService.getGenresByFilmId(created.getId());

        assertThat(genres).hasSize(2);
        assertThat(genres).extracting(Genre::getId).containsExactlyInAnyOrder(1L, 2L);
    }
}