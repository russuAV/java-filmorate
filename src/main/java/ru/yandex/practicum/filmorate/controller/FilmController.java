package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;


import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    private final Map<Long, Film> films = new HashMap<>();

    @GetMapping
    public Collection<Film> findAll() {
        return films.values();
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        log.info("Попытка добавления нового фильма в коллекцию: {}", film.getName());
        if (film.getId() != null && films.containsKey(film.getId())) {
            log.error("Ошибка добавления: Фильм уже имеется в коллекции");
            throw new ValidationException("Фильм уже имеется в коллекции");
        }
        film.setId(getNextId());
        films.put(film.getId(), film);
        film.setId(getNextId());
        films.put(film.getId(), film);
        log.info("Фильм: '{}', успешно добавлен в коллекцию", film.getName());
        return film;
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film filmWithNewData) {
        if (filmWithNewData.getId() == null || filmWithNewData.getId() == 0) {
            throw new ValidationException("id должен быть указан");
        }
        log.info("Попытка обновления данных о фильме: '{}'", filmWithNewData.getName());

        Film filmWithOldData = films.get(filmWithNewData.getId());
        if (filmWithOldData == null) {
            log.error("Ошибка обновления: фильм не найден");
            throw new ValidationException("Фильм не найден");
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

    private long getNextId() {
        long currentMaxId = films.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
