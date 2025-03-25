package ru.yandex.practicum.filmorate.model.filmdata;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Genre {
    private int id;
    private String name;
}