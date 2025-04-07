package ru.yandex.practicum.filmorate.model.filmdata;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MpaRating {
    private Long id;

    @JsonIgnoreProperties(ignoreUnknown = true)
    private String name;
}