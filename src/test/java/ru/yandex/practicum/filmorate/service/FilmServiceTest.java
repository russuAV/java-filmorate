package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.dto.NewFilmRequest;
import ru.yandex.practicum.filmorate.dto.UpdateFilmRequest;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.filmdata.Genre;
import ru.yandex.practicum.filmorate.model.filmdata.MpaRating;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@AutoConfigureTestDatabase
class FilmServiceTest {

    @Autowired
    private FilmService filmService;

    @Test
    void shouldDeleteFilmById() {
        Film film = filmService.create(new NewFilmRequest(
                "Test Film", "Description",
                LocalDate.of(2000, 1, 1), 90,
                new MpaRating(1L, null), List.of(new Genre(1L, ""))));

        filmService.delete(film.getId());

        assertThrows(NotFoundException.class, () -> filmService.getFilmById(film.getId()));
    }

    @Test
    void shouldCreateAndGetFilmById() {
        Film film = filmService.create(new NewFilmRequest(
                "Find Me", "Some Desc",
                LocalDate.of(1999, 5, 15), 100,
                new MpaRating(1L, null), List.of(new Genre(1L, "'"))));

        Film found = filmService.getFilmById(film.getId());

        assertThat(found).isNotNull();
        assertThat(found.getName()).isEqualTo("Find Me");
    }

    @Test
    void shouldThrowWhenGettingNonexistentFilm() {
        assertThrows(NotFoundException.class, () -> filmService.getFilmById(9999L));
    }

    @Test
    void shouldUpdateFilm() {
        Film created = filmService.create(new NewFilmRequest("Old Title", "Old Desc",
                LocalDate.of(2010, 1, 1), 100,
                new MpaRating(1L, null), List.of(new Genre(1L, null))));

        UpdateFilmRequest updateRequest = new UpdateFilmRequest(
                created.getId(),
                "New Title",
                "New Desc",
                created.getReleaseDate(),
                created.getDuration(),
                created.getMpaRating(),
                created.getGenres()
        );
        Film updated = filmService.update(updateRequest);

        assertThat(updated.getName()).isEqualTo("New Title");
        assertThat(updateRequest.getDescription()).isEqualTo("New Desc");
    }

    @Test
    void shouldThrowWhenUpdatingNonexistentFilm() {
        UpdateFilmRequest badUpdate = new UpdateFilmRequest(9999L, "Name", "Desc",
                LocalDate.of(2000, 1, 1), 100, new MpaRating(1L, ""),
                List.of(new Genre(1L, "")));
        assertThrows(NotFoundException.class, () -> filmService.update(badUpdate));
    }

    @Test
    void shouldThrowWhenGetNonexistentFilm() {
        assertThrows(NotFoundException.class, () -> filmService.getFilmById(-999L));
    }

    @Test
    void shouldThrowWhenDeleteNonexistentFilm() {
        assertThrows(NotFoundException.class, () -> filmService.delete(-1L));
    }

    @Test
    void shouldReturnEmptyListWhenNoFilmsByGenre() {
        assertThrows(NotFoundException.class, () -> filmService.getFilmsByGenreId(999L));
    }

    @Test
    void shouldGetFilmsByGenreId() {
        Film film = filmService.create(new NewFilmRequest("Comedy Film", "Funny",
                LocalDate.of(2010, 6, 10), 120,
                new MpaRating(1L, null), List.of(new Genre(1L, null))
        ));

        List<Film> films = filmService.getFilmsByGenreId(1L);

        assertThat(films).extracting(Film::getId).contains(film.getId());
    }

    @Test
    void shouldGetFilmsByGenreIds() {
        Film film1 = filmService.create(new NewFilmRequest("Film A", "Desc",
                LocalDate.of(2001, 1, 1), 110,
                new MpaRating(1L, null), List.of(new Genre(1L, null))
        ));

        Film film2 = filmService.create(new NewFilmRequest("Film B", "Desc",
                LocalDate.of(2002, 2, 2), 115,
                new MpaRating(1L, null), List.of(new Genre(2L, null))
        ));

        List<Film> films = filmService.getFilmsByGenreIds(List.of(1L, 2L));

        assertThat(films).extracting(Film::getId).contains(film1.getId(), film2.getId());
    }

    @Test
    void shouldGetFilmsByMpaId() {
        Film film = filmService.create(new NewFilmRequest("Rated G", "Family",
                LocalDate.of(2005, 3, 3), 95,
                new MpaRating(1L, null), null));
        Film film1 = filmService.create(new NewFilmRequest("Rated G", "Family",
                LocalDate.of(2005, 3, 3), 95,
                new MpaRating(1L, null), null));

        List<Film> films = filmService.getFilmsByMpaRatingId(1L);

        assertThat(films).extracting(Film::getId).contains(film.getId()).contains(film1.getId());
    }
}