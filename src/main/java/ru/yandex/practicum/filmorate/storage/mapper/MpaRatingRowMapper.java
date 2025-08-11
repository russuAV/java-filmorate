package ru.yandex.practicum.filmorate.storage.mapper;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.filmdata.MpaRating;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class MpaRatingRowMapper implements RowMapper<MpaRating>, Serializable {
    @Override
    public MpaRating mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        MpaRating mpaRating = new MpaRating();
        mpaRating.setId(resultSet.getLong("id"));
        mpaRating.setName(resultSet.getString("name"));
        return mpaRating;
    }
}