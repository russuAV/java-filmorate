package ru.yandex.practicum.filmorate.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import ru.yandex.practicum.filmorate.model.filmdata.Genre;
import ru.yandex.practicum.filmorate.model.filmdata.MpaRating;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
public class NewFilmRequest {
    @NotBlank(message = "Название не может быть пустым")
    private String name;

    @NotBlank
    @Size(max = 200, message = "Максимальная длина описания — 200 символов")
    private String description;

    @NotNull
    @Past
    private LocalDate releaseDate;

    @NotNull
    @Positive(message = "Продолжительность фильма должна быть положительным числом")
    private Integer duration;

    @NotNull(message = "MPA рейтинг обязателен.")
    private MpaRating mpa;

    private List<Genre> genres;

    @AssertTrue(message = "Дата релиза не раньше 28 декабря 1895 года")
    public boolean isValidReleaseDate() {
        LocalDate minReleaseDate = LocalDate.of(1895,12,28);
        return !releaseDate.isBefore(minReleaseDate);
    }
}