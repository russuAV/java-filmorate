package ru.yandex.practicum.filmorate.storage.memory;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Long, Film> films = new HashMap<>();

    @Override
    public List<Film> findAll() {
        return films.values().stream()
                .toList();
    }

    @Override
    public Optional<Film> getFilmById(Long filmId) {
        log.debug("Попытка найти фильм с id {}.", filmId);
        Optional<Film> result = Optional.ofNullable(films.get(filmId));
        if (result.isPresent()) {
            log.info("Фильм с id {} найден.", filmId);
        } else {
            log.warn("Фильм с id {} не найден.", filmId);
        }
        return result;
    }

    @Override
    public Film create(Film film) {
        log.info("Попытка добавления нового фильма в коллекцию: {}", film.getName());
        if (film.getId() != null && films.containsKey(film.getId())) {
            log.error("Ошибка добавления: Фильм уже имеется в коллекции");
            throw new ValidationException("Фильм уже имеется в коллекции");
        }
        film.setId(getNextId());
        films.put(film.getId(), film);
        log.info("Фильм: '{}', успешно добавлен в коллекцию", film.getName());
        return film;
    }

    @Override
    public Film update(Film filmWithNewData) {
        if (filmWithNewData.getId() == null || filmWithNewData.getId() == 0) {
            throw new ValidationException("id должен быть указан");
        }
        log.info("Попытка обновления данных о фильме: '{}'", filmWithNewData.getName());

        Film filmWithOldData = films.get(filmWithNewData.getId());
        if (filmWithOldData == null) {
            log.error("Ошибка обновления: фильм не найден");
            throw new NotFoundException("Фильм не найден");
        }

        Film updateFilm = new Film(
                filmWithNewData.getId(),
                filmWithNewData.getReleaseDate(),
                filmWithNewData.getName(),
                filmWithNewData.getDescription(),
                filmWithNewData.getDuration(),
                filmWithNewData.getGenres(),
                filmWithNewData.getMpaRating()
        );
        if (!updateFilm.isValidReleaseDate()) {
            throw new ValidationException("Дата релиза должна быть не раньше 28 декабря 1895 года");
        }
        if (filmWithNewData.getLikes() != null && !filmWithNewData.getLikes().isEmpty()) {
            updateFilm.setLikes(new HashSet<>(filmWithNewData.getLikes()));
        } else {
            updateFilm.setLikes(filmWithOldData.getLikes());
        }
        films.remove(filmWithOldData.getId(), filmWithOldData);
        films.put(updateFilm.getId(), updateFilm);
        log.info("Данные о фильме '{}' успешно обновлены", filmWithOldData.getName());
        return updateFilm;
    }

    public void delete(Long filmId) {
        log.debug("Удаление фильма с id = {}", filmId);
        if (!films.containsKey(filmId)) {
            log.error("Фильм с id = {} не найден для удаления", filmId);
            throw new NotFoundException("Фильм с id " + filmId + " не найден.");
        }
        films.remove(filmId);
        log.info("Фильм с id = {} успешно удалён", filmId);
    }

    public long getNextId() {
        long currentMaxId = films.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

    @Override
    public List<Film> getFilmsByMpaRatingId(Long mpaRatingId) {
        return films.values().stream()
                .filter(film -> film.getMpaRating() != null
                        && film.getMpaRating().getId() == mpaRatingId)
                .collect(Collectors.toList());

    }

    @Override
    public List<Film> getFilmsByGenreId(Long genreId) {
        return films.values().stream()
                .filter(film -> film.getGenres() != null
                        && film.getGenres().stream().anyMatch(genre -> genre.getId() == genreId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Film> getFilmsByGenreIds(List<Long> genreIds) {
        return films.values().stream()
                .filter(film -> film.getGenres() != null
                        && film.getGenres().stream()
                                .anyMatch(genre -> genreIds.contains(genre.getId())))
                .collect(Collectors.toList());
    }
}