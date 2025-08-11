package ru.yandex.practicum.filmorate.storage.db;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.model.filmdata.MpaRating;
import ru.yandex.practicum.filmorate.storage.mapper.MpaRatingRowMapper;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@Import({MpaRatingDbStorage.class, MpaRatingRowMapper.class})
class MpaRatingDbStorageTest {

    @Autowired
    private MpaRatingDbStorage mpaRatingDbStorage;

    @Autowired
    private JdbcTemplate jdbc;

    @Test
    void getAllShouldReturnAllRatings() {
        List<MpaRating> ratings = mpaRatingDbStorage.findAll();

        assertThat(ratings).isNotEmpty();
        assertThat(ratings)
                .extracting(MpaRating::getName)
                .contains("G", "PG", "PG-13", "R", "NC-17");
    }

    @Test
    void getByIdShouldReturnCorrectRating() {
        Optional<MpaRating> mpa = mpaRatingDbStorage.getById(3L);

        assertThat(mpa).isPresent();
        assertThat(mpa.get().getId()).isEqualTo(3L);
        assertThat(mpa.get().getName()).isEqualTo("PG-13");
    }

    @Test
    void getByIdShouldReturnEmptyForUnknownId() {
        Optional<MpaRating> mpa = mpaRatingDbStorage.getById(999L);

        assertThat(mpa).isEmpty();
    }
}