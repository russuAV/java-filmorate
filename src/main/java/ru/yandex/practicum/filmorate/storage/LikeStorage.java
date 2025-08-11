package ru.yandex.practicum.filmorate.storage;

import java.util.List;

public interface LikeStorage {
    void addLike(Long filmId, Long userId);

    void removeLike(Long filmId, Long userId);

    List<Long> getUserIdsWhoLikedFilm(Long filmId);

    boolean existLike(Long filmId, Long userId);

    List<Long> getTopFilmIds(int count);
}