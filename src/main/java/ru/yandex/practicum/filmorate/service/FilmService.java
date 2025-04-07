package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.NewFilmRequest;
import ru.yandex.practicum.filmorate.dto.UpdateFilmRequest;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.filmdata.Genre;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.LikeStorage;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmService {
    private final FilmStorage filmStorage;
    private final MpaRatingService mpaRatingService;
    private final GenreService genreService;
    private final UserService userService;
    private final LikeStorage likeStorage;

    public List<Film> findAll() {
        log.debug("Попытка получить список всех фильмов.");
        List<Film> films = filmStorage.findAll();
        for (Film film : films) {
            List<Genre> genres = genreService.getGenresByFilmId(film.getId());
            film.setGenres(genres);
            film.setMpaRating(mpaRatingService.getMpaRatingById(film.getMpaRating().getId()));
        }

        log.info("Список фильмов {}.", films.isEmpty() ? "пока пустой" : "успешно получен.");
        return films;
    }

    public Film create(NewFilmRequest request) {
        log.debug("Попытка создать фильм: {}", request);
        if (request.getGenres() != null) {
            genreService.validateGenres(request.getGenres());
        }
        mpaRatingService.getMpaRatingById(request.getMpa().getId());
        Film film = FilmMapper.mapToFilm(request);

        log.info("Фильм создан: {}", request);
        return filmStorage.create(film);
    }

    public Film update(UpdateFilmRequest request) {
        log.debug("Попытка обновить информацию фильма: {}", request);
        Film existing = filmStorage.getFilmById(request.getId())
                .orElseThrow(() -> new NotFoundException("Фильм с id " + request.getId() + " не найден."));
        Film updated = FilmMapper.updateFilmFields(existing, request);

        log.info("Успешное обновлении информации фильма: {}", request);
        return filmStorage.update(updated);
    }

    public void delete(Long id) {
        log.debug("Удаление фильма с id = {}", id);
        getFilmById(id);
        filmStorage.delete(id);
        log.info("Фильм с id = {} успешно удалён", id);
    }

    public Film getFilmById(Long id) {
        log.debug("Попытка получить фильм с id: {}", id);
        Film film = filmStorage.getFilmById(id)
                .orElseThrow(() -> new NotFoundException("Фильм с id " + id + " не найден."));
        film.setGenres(genreService.getGenresByFilmId(id));
        film.setMpaRating(mpaRatingService.getMpaRatingById(film.getMpaRating().getId()));

        log.info("Фильм с id {} успешно получен", id);
        return film;
    }

    public List<Film> getFilmsByGenreId(Long genreId) {
        log.info("Попытка получить список фильмов с жанром {}", genreId);
        genreService.getGenreById(genreId);
        List<Film> films = filmStorage.getFilmsByGenreId(genreId);

        if (films.isEmpty()) {
            log.info("Фильмы с жанром id = {} не найдены.", genreId);
        } else {
            log.info("Список фильмов с жанром id = {} успешно получен. Кол-во: {}", genreId, films.size());
        }

        return films;
    }

    public List<Film> getFilmsByGenreIds(List<Long> genreIds) {
        log.info("Попытка получить список фильмов с жанрами {}", genreIds);
        genreIds.forEach(genreService::getGenreById);
        List<Film> films = filmStorage.getFilmsByGenreIds(genreIds);
        if (films.isEmpty()) {
            log.info("Фильмы с жанрами {} не найдены.", genreIds);
        } else {
            log.info("Список фильмов с жанрами {} успешно получен. Кол-во: {}", genreIds, films.size());
        }

        return films;
    }

    public List<Film> getFilmsByMpaRatingId(Long mpaRatingId) {
        log.info("Попытка получить список фильмов с рейтингом {}", mpaRatingId);
        mpaRatingService.getMpaRatingById(mpaRatingId);
        List<Film> films = filmStorage.getFilmsByMpaRatingId(mpaRatingId);
        if (films.isEmpty()) {
            log.info("Фильмы с рейтингом {} не найдены.", mpaRatingId);
        } else {
            log.info("Список фильмов с рейтингом {} успешно получен. Кол-во: {}", mpaRatingId, films.size());
        }

        return films;
    }

    public List<User> getUsersWhoLikedFilm(Long filmId) {
        log.debug("Попытка получить список пользователей, кому понравился фильм с id {}.", filmId);
        getFilmById(filmId);
        List<Long> userIds = likeStorage.getUserIdsWhoLikedFilm(filmId);
        List<User> users = userIds.stream()
                .map(userService::getUserById)
                .toList();

        log.info("Список пользователей, кому понравился фильм с id {} получен.", filmId);
        return users;
    }

    public List<Film> getTopFilms(int count) {
        log.debug("Запрос на получение топ фильмов");
        List<Long> topFilmIds = likeStorage.getTopFilmIds(count);
        List<Film> topFilms = topFilmIds.stream()
                .map(this::getFilmById)
                .toList();

        log.info("Выведен список из {} топ фильмов", topFilms.size());
        return topFilms;
    }
}