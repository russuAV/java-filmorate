package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.filmdata.Genre;

import java.util.List;
import java.util.Optional;

public interface GenreStorage {
    List<Genre> findAll();

    Optional<Genre> getById(long genreId);

    List<Genre> getGenresByFilmId(Long filmId);
}