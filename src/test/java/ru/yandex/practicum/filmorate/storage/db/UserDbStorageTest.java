package ru.yandex.practicum.filmorate.storage.db;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.mapper.UserRowMapper;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@Import({UserDbStorage.class, UserRowMapper.class})
public class UserDbStorageTest {

    @Autowired
    private UserDbStorage userStorage;
    @Autowired
    private JdbcTemplate jdbc;

    @Test
    void testCreateAndFindUserById() {
        User newUser = new User();
        newUser.setEmail("test@example.com");
        newUser.setLogin("test_login");
        newUser.setName("Test User");
        newUser.setBirthday(LocalDate.of(1990, 1, 1));

        User createdUser = userStorage.create(newUser);
        Optional<User> foundUser = userStorage.getUserById(createdUser.getId());

        assertThat(foundUser)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user)
                                .hasFieldOrPropertyWithValue("id", createdUser.getId())
                                .hasFieldOrPropertyWithValue("email", "test@example.com")
                                .hasFieldOrPropertyWithValue("login", "test_login")
                                .hasFieldOrPropertyWithValue("name", "Test User")
                                .hasFieldOrPropertyWithValue("birthday",
                                        LocalDate.of(1990, 1, 1))
                );
    }

    @Test
    void updateUser() {
        User user = new User(null, "original@email.com", "orig", "Original",
                LocalDate.of(1985, 5, 5));
        User created = userStorage.create(user);

        created.setEmail("updated@email.com");
        created.setName("Updated");
        userStorage.update(created);

        Optional<User> updated = userStorage.getUserById(created.getId());
        assertThat(updated).isPresent();
        assertThat(updated.get().getEmail()).isEqualTo("updated@email.com");
        assertThat(updated.get().getName()).isEqualTo("Updated");
    }

    @Test
    void deleteUser() {
        User user = userStorage.create(new User(null, "todelete@email.com", "todelete",
                "Delete Me", LocalDate.of(1999, 9, 9)));
        userStorage.delete(user.getId());

        Optional<User> deleted = userStorage.getUserById(user.getId());
        assertThat(deleted).isEmpty();
    }

    @Test
    void findAllUsers() {
        jdbc.update("DELETE FROM users");

        userStorage.create(new User(null, "one@email.com", "one", "One",
                LocalDate.of(1990, 1, 1)));
        userStorage.create(new User(null, "two@email.com", "two", "Two",
                LocalDate.of(1992, 2, 2)));

        List<User> users = userStorage.findAll();
        assertThat(users).hasSize(2);
    }
}