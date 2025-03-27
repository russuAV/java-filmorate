package ru.yandex.practicum.filmorate.model.filmdata;

public enum MpaRating {
    G("G — без ограничений"),
    PG("PG — с родителями"),
    PG_13("PG-13 — не рекомендован до 13"),
    R("R — до 17 с родителями"),
    NC_17("NC-17 — строго 18+");

    private final String description;

    MpaRating(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}