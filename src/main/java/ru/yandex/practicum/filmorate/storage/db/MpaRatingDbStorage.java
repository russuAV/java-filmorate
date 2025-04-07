package ru.yandex.practicum.filmorate.storage.db;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.filmdata.MpaRating;
import ru.yandex.practicum.filmorate.storage.BaseBdStorage;
import ru.yandex.practicum.filmorate.storage.MpaRatingStorage;

import java.util.List;
import java.util.Optional;

@Repository
public class MpaRatingDbStorage extends BaseBdStorage<MpaRating> implements MpaRatingStorage {

    private static final String FIND_ALL_QUERY = """
            SELECT *
            FROM mpa_ratings
            """;
    private static final String FIND_BY_ID_QUERY = """
            SELECT *
            FROM mpa_ratings
            WHERE id = ?
            """;

    public MpaRatingDbStorage(JdbcTemplate jdbc, RowMapper<MpaRating> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public List<MpaRating> findAll() {
        return findMany(FIND_ALL_QUERY);
    }

    @Override
    public Optional<MpaRating> getById(long mpaId) {
        return findOne(FIND_BY_ID_QUERY, mpaId);
    }
}