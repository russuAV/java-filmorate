package ru.yandex.practicum.filmorate.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Past;
import lombok.Data;
import ru.yandex.practicum.filmorate.model.filmdata.Genre;
import ru.yandex.practicum.filmorate.model.filmdata.MpaRating;

import java.time.LocalDate;
import java.util.List;

@Data
public class UpdateFilmRequest {
    private final Long id;
    private final String name;
    private final String description;

    @Past
    private final LocalDate releaseDate;

    private final Integer duration;
    private final MpaRating mpa;
    private final List<Genre> genres;

    public boolean hasName() {
        return name != null && !name.isBlank();
    }

    public boolean hasDescription() {
        return description != null && !description.isBlank();
    }

    public boolean hasReleaseDate() {
        return releaseDate != null;
    }

    public boolean hasDuration() {
        return duration != null && duration > 0;
    }

    public boolean hasMpaRating() {
        return mpa != null;
    }

    public boolean hasGenres() {
        return genres != null;
    }

    public boolean hasId() {
        return id != null;
    }

    @AssertTrue(message = "Дата релиза не раньше 28 декабря 1895 года")
    public boolean isValidReleaseDate() {
        LocalDate minReleaseDate = LocalDate.of(1895,12,28);
        return !releaseDate.isBefore(minReleaseDate);
    }
}