package ru.yandex.practicum.filmorate.dto;

import lombok.Data;
import ru.yandex.practicum.filmorate.model.filmdata.Genre;
import ru.yandex.practicum.filmorate.model.filmdata.MpaRating;

import java.time.LocalDate;
import java.util.List;

@Data
public class FilmDto {
    private long id;
    private String name;
    private String description;
    private LocalDate releaseDate;
    private int duration;
    private MpaRating mpa;
    private List<Genre> genres;
}