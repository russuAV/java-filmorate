package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.dto.NewUserRequest;
import ru.yandex.practicum.filmorate.dto.UpdateUserRequest;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@AutoConfigureTestDatabase
class UserServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private JdbcTemplate jdbc;

    @BeforeEach
    void setUp() {
        jdbc.update("DELETE FROM friendships");
        jdbc.update("DELETE FROM users");
    }

    @Test
    void shouldCreateUserAndReturn() {
        NewUserRequest request = new NewUserRequest(
                "test@mail.com",
                "login",
                "Name",
                LocalDate.of(2000, 1, 1)
        );

        User user = userService.create(request);

        assertThat(user.getId()).isNotNull();
        assertThat(user.getLogin()).isEqualTo("login");
        assertThat(user.getEmail()).isEqualTo("test@mail.com");
    }

    @Test
    void shouldReturnAllUsers() {
        userService.create(new NewUserRequest("a@mail.com", "a", "A",
                LocalDate.of(1990, 1, 1)));
        userService.create(new NewUserRequest("b@mail.com", "b", "B",
                LocalDate.of(1992, 2, 2)));

        List<User> all = userService.findAll();
        assertThat(all).hasSize(2);
    }

    @Test
    void shouldUpdateUser() {
        User created = userService.create(new NewUserRequest(
                "mail@mail.com", "login", "Old",
                LocalDate.of(1995, 3, 3)));

        UpdateUserRequest updateRequest = new UpdateUserRequest(
                created.getId(), "mail@mail.com", "login",
                "Updated Name", LocalDate.of(1995, 3, 3)
        );

        User updated = userService.update(updateRequest);

        assertThat(updated.getName()).isEqualTo("Updated Name");
        assertThat(updated.getId()).isEqualTo(created.getId());
    }

    @Test
    void shouldThrowOnUpdatingMissingUser() {
        UpdateUserRequest request = new UpdateUserRequest(999L, "email", "login",
                "name", null);
        assertThrows(NotFoundException.class, () -> userService.update(request));
    }

    @Test
    void shouldFindById() {
        User created = userService.create(new NewUserRequest("id@mail.com", "log", "X",
                LocalDate.of(1991, 1, 1)));
        User found = userService.getUserById(created.getId());

        assertThat(found).isNotNull();
        assertThat(found.getLogin()).isEqualTo("log");
    }
}