package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.storage.LikeStorage;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class LikeService {
    private final FilmService filmService;
    private final UserService userService;
    private final LikeStorage likeStorage;

    public void addLike(Long filmId, Long userId) {
        log.debug("Попытка добавить лайк фильму {} от пользователя {}.", filmId, userId);
        validateFilmAndUserExist(filmId, userId);

        if (likeStorage.existLike(filmId, userId)) {
            log.error("Пользователь с id {} уже добавил лайк фильму {}.", userId, filmId);
            throw new ValidationException("Данный пользователь уже добавлял лайк этому фильму!");
        }
        likeStorage.addLike(filmId, userId);
        log.info("Пользователь {} поставил лайк фильму {}.", userId, filmId);
    }

    public void deleteLike(Long filmId, Long userId) {
        log.debug("Попытка удалить лайк у фильма {} от пользователя {}.", filmId, userId);
        validateFilmAndUserExist(filmId, userId);

        if (!likeStorage.existLike(filmId, userId)) {
            log.error("Пользователь с id {} не ставил лайк фильму {}.", userId, filmId);
            throw new ValidationException("Этот пользователь не ставил лайк данному фильму");
        }
        likeStorage.removeLike(filmId, userId);
        log.info("Пользователь {} удалил лайк у фильма {}.", userId, filmId);
    }


    public List<Long> getUserIdsWhoLikedFilm(Long filmId) {
        log.debug("Попытка получить список id пользователей, кому понравился фильм с id {}", filmId);
        List<Long> userIds = likeStorage.getUserIdsWhoLikedFilm(filmId);

        log.info("Получен список id пользователей, кому понравился фильм c id {}", filmId);
        return userIds;
    }

    public List<Long> getTopFilmIds(int count) {
        log.debug("Попытка получить топ-список id фильмов по количеству лайков");
        List<Long> topFilmIds = likeStorage.getTopFilmIds(count);

        log.info("Получен топ-список id фильмов по количеству лайков.");
        return topFilmIds;
    }

    public void validateFilmAndUserExist(Long filmId, Long userId) {
        log.trace("Валидация существования фильма с id {} и пользователя с id {}", filmId, userId);
        if (filmId == null || userId == null) {
            log.error("Оба идентификатора (filmId и userId) должны быть указаны");
            throw new ValidationException("Id должен быть указан");
        }

        filmService.getFilmById(filmId);
        userService.getUserById(userId);

        log.trace("Валидация успешна для фильма с id {} и пользователя с id {}", filmId, userId);
    }
}