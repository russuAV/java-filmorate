package ru.yandex.practicum.filmorate.storage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryFilmStorageTest {
private FilmStorage filmStorage;

@BeforeEach
    void setUp() {
        filmStorage = new InMemoryFilmStorage() {
        };
    }


    @Test
    void findAll() {
        Film film1 = new Film(1L, LocalDate.of(1997, 1, 26),
                "test1", "description1", 120);
        Film film2 = new Film(2L, LocalDate.of(1997, 1, 26),
                "test2", "description2", 120);
        filmStorage.create(film1);
        filmStorage.create(film2);

        assertEquals(2, filmStorage.findAll().size());
        assertEquals("test1", filmStorage.getFilmById(1L).getName());
        assertEquals("test2", filmStorage.getFilmById(2L).getName());
    }

    @Test
    void getFilmById() {
        Film film1 = new Film(1L, LocalDate.of(1997, 1, 26),
                "test1", "description1", 120);
        filmStorage.create(film1);

        assertEquals(film1, filmStorage.getFilmById(1L));
    }

    @Test
    void getFilmByIdShouldThrowException() {
    Exception exception = assertThrows(NotFoundException.class, () -> {
        filmStorage.getFilmById(1L);
    });
    assertEquals("Фильм с id 1 не найден.", exception.getMessage());
    }

    @Test
    void create() {
        Film film1 = new Film(1L, LocalDate.of(1997, 1, 26),
                "test1", "description1", 120);
        filmStorage.create(film1);

        assertEquals(1, filmStorage.findAll().size());
        assertEquals("test1", filmStorage.getFilmById(1L).getName());
    }

    @Test
    void update() {
        Film film1 = new Film(1L, LocalDate.of(1997, 1, 26),
                "test1", "description1", 120);

        filmStorage.create(film1);

        Film updateFilm1 = new Film(1L, LocalDate.of(1997, 1, 26),
                "updateFilm1", "updateDescription1", 130);


        filmStorage.update(updateFilm1);

        assertEquals("updateFilm1", filmStorage.getFilmById(1L).getName());
        assertEquals("updateDescription1", filmStorage.getFilmById(1L).getDescription());
        assertEquals(130, filmStorage.getFilmById(1L).getDuration());
    }
}