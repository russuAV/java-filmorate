package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Comparator;
import java.util.List;

@Slf4j
@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public void addLike(Long filmId, Long userId) {
        log.debug("Попытка добавить лайк фильму {} от пользователя {}.", filmId, userId);
        validateFilmAndUserExist(filmId, userId);
        Film film = filmStorage.getFilmById(filmId);

        if (!film.getLikes().add(userId)) {
            log.error("Пользователь с id {} уже добавил лайк фильму {}.", userId, filmId);
            throw new ValidationException("Данный пользователь уже добавлял лайк этому фильму!");
        }
        log.info("Пользователь {} поставил лайк фильму {}.", userId, filmId);
    }

    public void deleteLike(Long filmId, Long userId) {
        log.debug("Попытка удалить лайк у фильма {} от пользователя {}.", filmId, userId);
        validateFilmAndUserExist(filmId, userId);
        Film film = filmStorage.getFilmById(filmId);

        if (!film.getLikes().remove(userId)) {
            log.error("Пользователь с id {} не ставил лайк фильму {}.", userId, filmId);
            throw new ValidationException("Этот пользователь не ставил лайк данному фильму");
        }
        log.info("Пользователь {} удалил лайк у фильма {}.", userId, filmId);
    }

    public List<Film> getTopFilms(int count) {
        log.debug("Запрос на получение топ фильмов");
        List<Film> topFilms = filmStorage.findAll().stream()
                .sorted(Comparator.comparingInt(film -> -film.getLikes().size()))
                .limit(count)
                .toList();
        log.info("Выведен список из {} топ фильмов", topFilms.size());
        return topFilms;
    }

    public void validateFilmAndUserExist(Long filmId, Long userId) {
        log.trace("Валидация существования фильма с id {} и пользователя с id {}", filmId, userId);
        if (filmId == null || userId == null) {
            log.error("Оба идентификатора (filmId и userId) должны быть указаны");
            throw new ValidationException("Id должен быть указан");
        }
        try {
            filmStorage.getFilmById(filmId);
        } catch (NotFoundException e) {
            log.error("Фильм с id {} не найден.", filmId);
            throw e;
        }
        try {
            userStorage.getUserById(userId);
        } catch (NotFoundException e) {
            log.error("Пользователь с id {} не найден.", userId);
            throw e;
        }
        log.trace("Валидация успешна для фильма с id {} и пользователя с id {}", filmId, userId);
    }
}