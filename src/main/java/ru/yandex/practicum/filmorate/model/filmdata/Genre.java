package ru.yandex.practicum.filmorate.model.filmdata;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Genre {
    private final int id;
    private final String name;
}