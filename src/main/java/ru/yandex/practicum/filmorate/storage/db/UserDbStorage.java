package ru.yandex.practicum.filmorate.storage.db;

import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.BaseBdStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;
import java.util.Optional;

@Repository
@Primary
public class UserDbStorage extends BaseBdStorage<User> implements UserStorage {

    private static final String FIND_ALL_QUERY = """
    SELECT *
    FROM users
    """;
    private static final String FIND_BY_EMAIL_QUERY = """
    SELECT *
    FROM users
    WHERE email = ?
    """;

    private static final String FIND_BY_ID_QUERY = """
    SELECT *
    FROM users
    WHERE id = ?
    """;

    private static final String INSERT_QUERY = """
            INSERT INTO users(email, login, name, birthday)
            VALUES (?, ?, ?, ?)
            """;
    private static final String UPDATE_QUERY = """
            UPDATE users SET email = ?, login = ?, name = ?, birthday = ?
            WHERE id = ?
            """;
    private static final String DELETE_USER_QUERY = """
            DELETE FROM users
            WHERE id = ?
            """;
    private static final String DELETE_USER_LIKES_QUERY = """
            DELETE FROM likes
            WHERE user_id = ?
            """;
    private static final String DELETE_USER_FRIENDSHIPS_QUERY = """
            DELETE FROM friendships
            WHERE sender_id = ? OR receiver_id = ?
            """;

    public UserDbStorage(JdbcTemplate jdbc, RowMapper<User> mapper) {
        super(jdbc, mapper);
    }

    public List<User> findAll() {
        return findMany(FIND_ALL_QUERY);
    }

    public User create(User user) {
        long id = insert(
                INSERT_QUERY,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday()
        );
        user.setId(id);

        return user;
    }

    public User update(User user) {
        update(
                UPDATE_QUERY,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday(),
                user.getId()
        );

        return user;
    }

    public void delete(Long userId) {
        jdbc.update(DELETE_USER_LIKES_QUERY, userId);
        jdbc.update(DELETE_USER_FRIENDSHIPS_QUERY, userId, userId);
        jdbc.update(DELETE_USER_QUERY, userId);
    }

    public Optional<User> getUserByEmail(String email) {
        return findOne(FIND_BY_EMAIL_QUERY, email);
    }

    public Optional<User> getUserById(Long userId) {
        return findOne(FIND_BY_ID_QUERY, userId);
    }
}