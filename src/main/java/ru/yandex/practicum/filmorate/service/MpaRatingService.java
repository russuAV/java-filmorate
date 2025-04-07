package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.filmdata.MpaRating;
import ru.yandex.practicum.filmorate.storage.MpaRatingStorage;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MpaRatingService {
    private final MpaRatingStorage mpaRatingStorage;

    public List<MpaRating> findAll() {
        log.debug("Попытка получить список всех рейтингов.");
        List<MpaRating> mpaRatings = mpaRatingStorage.findAll();

        log.info("Получен список всех рейтингов.");
        return mpaRatings;
    }

    public MpaRating getMpaRatingById(Long mpaRatingId) {
        log.debug("Попытка получить рейтинг с id: {}.", mpaRatingId);
        MpaRating mpaRating = mpaRatingStorage.getById(mpaRatingId)
                .orElseThrow(() -> new NotFoundException("Рейтинг с id " + mpaRatingId + " не найден."));

        log.info("Рейтинг с id {} успешно получен.", mpaRatingId);
        return mpaRating;
    }
}