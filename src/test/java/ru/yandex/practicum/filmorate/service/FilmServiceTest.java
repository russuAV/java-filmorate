package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.filmdata.Genre;
import ru.yandex.practicum.filmorate.model.filmdata.MpaRating;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class FilmServiceTest {
    private FilmStorage filmStorage;
    private FilmService filmService;
    private UserStorage userStorage;

    @BeforeEach
    void setUp() {
        filmStorage = new InMemoryFilmStorage();
        userStorage = new InMemoryUserStorage();
        filmService = new FilmService(filmStorage, userStorage);
    }

    @Test
    void addLike() {
        Film film = new Film(1L, LocalDate.of(1997, 1, 26),
                "test", "description", 120,
                Set.of(new Genre(1, "Comedy")), MpaRating.G);

        User user = userStorage.create(new User(1L, "user1@email.com", "user1", "user1",
                LocalDate.of(1997,1,26)));
        filmStorage.create(film);
        filmService.addLike(film.getId(), user.getId());

        assertEquals(1, filmStorage.getFilmById(film.getId()).getLikes().size());
    }

    @Test
    void deleteLike() {
        Film film = new Film(1L, LocalDate.of(1997, 1, 26),
                "test", "description", 120,
                Set.of(new Genre(1, "Comedy")), MpaRating.G);

        User user = userStorage.create(new User(1L, "user1@email.com", "user1", "user1",
                LocalDate.of(1997,1,26)));
        filmStorage.create(film);
        filmService.addLike(film.getId(), user.getId());
        filmService.deleteLike(film.getId(), user.getId());

        assertEquals(0, filmStorage.getFilmById(film.getId()).getLikes().size());

    }

    @Test
    void getTopFilms() {
        Film film1 = new Film(1L, LocalDate.of(1997, 1, 26),
                "test1", "description1", 120,
                Set.of(new Genre(1, "Comedy")), MpaRating.G);
        Film film2 = new Film(2L, LocalDate.of(1997, 1, 26),
                "test2", "description2", 120,
                Set.of(new Genre(1, "Comedy")), MpaRating.G);

        User user = userStorage.create(new User(1L, "user1@email.com", "user1", "user1",
                LocalDate.of(1997,1,26)));

        filmStorage.create(film1);
        filmStorage.create(film2);

        filmService.addLike(film1.getId(), user.getId());

        List<Film> topFilms = filmService.getTopFilms(2);

        assertEquals(2, topFilms.size());
        assertEquals("test1", topFilms.getFirst().getName());

    }
}