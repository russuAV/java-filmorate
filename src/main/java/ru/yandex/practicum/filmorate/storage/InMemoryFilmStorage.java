package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Long, Film> films = new HashMap<>();

    @Override
    public Collection<Film> findAll() {
        return films.values();
    }

    @Override
    public Film getFilmById(Long id) {
        log.debug("Попытка получить фильм с id {}.", id);
        Film film = films.get(id);
        if (film == null) {
            log.error("Фильм с id {} не найден.", id);
            throw new NotFoundException("Фильм с id " + id + " не найден.");
        }
        log.info("Получен фильм с id {}.", id);
        return film;
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
        return film;    }

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
        // меняем релиз-дату
        if (filmWithNewData.isValidReleaseDate()) {
            filmWithOldData.setReleaseDate(filmWithNewData.getReleaseDate());
        }
        // меняем название
        filmWithOldData.setName(filmWithNewData.getName());

        //меняем описание
        filmWithOldData.setDescription(filmWithNewData.getDescription());

        //меняем продолжительность фильма
        filmWithOldData.setDuration(filmWithNewData.getDuration());
        log.info("Данные о фильме '{}' успешно обновлены", filmWithOldData.getName());
        return filmWithOldData;
    }

    @Override
    public long getNextId() {
        long currentMaxId = films.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;    }
}
