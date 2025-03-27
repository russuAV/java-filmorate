package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import ru.yandex.practicum.filmorate.model.filmdata.Genre;
import ru.yandex.practicum.filmorate.model.filmdata.MpaRating;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@RequiredArgsConstructor
public class Film {
    private Long id;
    private Set<Long> likes = new HashSet<>();
    private Set<Genre> genres = new HashSet<>();
    private MpaRating mpaRating;

    public Film(Long id, LocalDate releaseDate, String name, String description, int duration,
                Set<Genre> genres, MpaRating mpaRating) {
        this.id = id;
        this.releaseDate = releaseDate;
        this.name = name;
        this.description = description;
        this.duration = duration;
        this.genres = genres;
        this.mpaRating = mpaRating;
    }

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