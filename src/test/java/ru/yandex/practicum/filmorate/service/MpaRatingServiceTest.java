package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.filmdata.MpaRating;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@AutoConfigureTestDatabase
class MpaRatingServiceTest {

    @Autowired
    private MpaRatingService mpaRatingService;
    @Autowired
    private FilmService filmService;

    @Test
    void shouldReturnAllMpaRatings() {
        List<MpaRating> ratings = mpaRatingService.findAll();
        assertThat(ratings).isNotEmpty();
        assertThat(ratings).extracting(MpaRating::getId).contains(1L, 2L, 3L, 4L, 5L);
    }

    @Test
    void shouldReturnMpaRatingById() {
        MpaRating rating = mpaRatingService.getMpaRatingById(1L);
        assertThat(rating).isNotNull();
        assertThat(rating.getId()).isEqualTo(1L);
        assertThat(rating.getName()).isNotBlank();
    }

    @Test
    void shouldThrowExceptionIfMpaRatingNotFound() {
        assertThatThrownBy(() -> mpaRatingService.getMpaRatingById(999L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Рейтинг с id " + 999L + " не найден.");
    }
}