package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.filmdata.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class GenreService {
    private final GenreStorage genreStorage;

    public List<Genre> findAll() {
        log.debug("Попытка получить список всех жанров.");
        List<Genre> genres = genreStorage.findAll();

        log.info("Получен список всех жанров.");
        return genres;
    }

    public Genre getGenreById(Long genreId) {
        log.debug("Попытка получить жанр с id: {}", genreId);
        Genre genre = genreStorage.getById(genreId)
                .orElseThrow(() -> new NotFoundException("Жанр с id " + genreId + " не найден."));

        log.info("Жанр с id {} успешно получен", genreId);
        return genre;
    }

    public List<Genre> getGenresByFilmId(Long filmId) {
        log.debug("Попытка получить жанры фильма с id {}", filmId);
        List<Genre> genresFilm = genreStorage.getGenresByFilmId(filmId);

        log.info("Получен список жанров фильма с id {}", filmId);
        return genresFilm;
    }

    public void validateGenres(List<Genre> genres) {
        log.debug("Валидация жанров:");
        if (genres == null || genres.isEmpty()) {
            throw new NotFoundException("Жанр фильма должен быть указан.");
        }
        for (Genre genre : genres) {
            getGenreById(genre.getId());
        }
        log.info("Валидация жанров прошла успешно.");
    }
}