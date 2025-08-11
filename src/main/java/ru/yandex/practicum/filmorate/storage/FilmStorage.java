package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Optional;

public interface FilmStorage {

    List<Film> findAll();

    Optional<Film> getFilmById(Long filmId);

    List<Film> getFilmsByGenreId(Long genreId);

    List<Film> getFilmsByGenreIds(List<Long> genreIds);

    List<Film> getFilmsByMpaRatingId(Long mpaRatingId);

    Film create(Film film);

    Film update(Film filmWithNewData);

    void delete(Long filmId);
}