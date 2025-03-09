package ru.yandex.practicum.filmorate;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class FilmValidationTest {

    private Validator validator;

    @BeforeEach
    public void setup() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void testValidFilm() {
        Film film = new Film();
        film.setId(1L);
        film.setName("Inception");
        film.setDescription("A great movie.");
        film.setReleaseDate(LocalDate.of(2010, 7, 16));
        film.setDuration(148);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertTrue(violations.isEmpty(), "Корректный фильм должен проходить валидацию.");
    }

    @Test
    public void testNameCannotBeBlank() {
        Film film = new Film();
        film.setName("");
        film.setDescription("A great movie.");
        film.setReleaseDate(LocalDate.of(2010, 7, 16));
        film.setDuration(148);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty(), "Название фильма не может быть пустым.");
    }

    @Test
    public void testDescriptionMaxLength() {
        Film film = new Film();
        film.setName("Inception");
        film.setDescription("A".repeat(201)); // 201 символ
        film.setReleaseDate(LocalDate.of(2010, 7, 16));
        film.setDuration(148);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty(), "Описание фильма не должно превышать 200 символов.");
    }

    @Test
    public void testReleaseDateCannotBeBefore1895() {
        Film film = new Film();
        film.setName("Old Movie");
        film.setDescription("A great old movie.");
        film.setReleaseDate(LocalDate.of(1895, 12, 27)); // День до допустимой даты
        film.setDuration(148);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty(), "Дата релиза не может быть раньше 28 декабря 1895 года.");
    }

    @Test
    public void testDurationMustBePositive() {
        Film film = new Film();
        film.setName("Inception");
        film.setDescription("A great movie.");
        film.setReleaseDate(LocalDate.of(2010, 7, 16));
        film.setDuration(-10); // Отрицательная продолжительность

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty(), "Продолжительность фильма должна быть положительной.");
    }
}