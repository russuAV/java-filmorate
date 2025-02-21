package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

@Data
public class Film {
    private Long id;

    @NotNull
    private LocalDate releaseDate;

    @NotBlank(message = "Название не может быть пустым")
    private String name;

    @NotBlank
    @Size(max = 200, message = "Максимальная длина описания — 200 символов")
    private String description;

    @NotNull
    @Positive(message = "Продолжительность фильма должна быть положительным числом")
    private int duration;

    @AssertTrue(message = "Дата релиза не раньше 28 декабря 1895 года")
    public boolean isValidReleaseDate() {
        LocalDate minReleaseDate = LocalDate.of(1895,12,28);
        return !releaseDate.isBefore(minReleaseDate);
    }
}
