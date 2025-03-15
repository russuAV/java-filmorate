package ru.yandex.practicum.filmorate.storage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryUserStorageTest {
    private UserStorage userStorage;


    @BeforeEach
    void setUp() {
        userStorage = new InMemoryUserStorage();
    }

    @Test
    void findAll() {
        User user1 = new User(1L, "user1@email.com", "user1", "user1",
                LocalDate.of(1997, 01, 26));
        User user2 = new User(2L, "user2@email.com", "user2", "user2",
                LocalDate.of(1997, 01, 27));
        userStorage.create(user1);
        userStorage.create(user2);

        assertEquals(2, userStorage.findAll().size());
    }

    @Test
    void getUserById() {
        User user1 = new User(1L, "user1@email.com", "user1", "user1",
                LocalDate.of(1997, 01, 26));

        userStorage.create(user1);
        assertEquals(user1, userStorage.getUserById(user1.getId()));
    }

    @Test
    void getUserByIdShouldThrowException() {
        Exception exception = assertThrows(NotFoundException.class, () -> {
            userStorage.getUserById(1L);
    });
        assertEquals("Пользователь с id 1 не найден.", exception.getMessage());
    }

    @Test
    void create() {
        User user1 = new User(1L, "user1@email.com", "user1", "user1",
                LocalDate.of(1997, 01, 26));
        User user2 = new User(2L, "user2@email.com", "user2", "user2",
                LocalDate.of(1997, 01, 27));
        userStorage.create(user1);
        userStorage.create(user2);

        assertEquals(user1, userStorage.getUserById(1L));
        assertEquals(user2, userStorage.getUserById(2L));
        assertEquals("user1", userStorage.getUserById(1L).getName());
    }

    @Test
    void update() {
        User user1 = new User(1L, "user1@email.com", "user1", "user1",
                LocalDate.of(1997, 01, 26));
        User updateUser1 = new User(1L, "user1new@email.com", "newLogin1", "newUser1",
                LocalDate.of(1997, 01, 26));

        userStorage.create(user1);
        userStorage.update(updateUser1);

        assertEquals("newUser1", userStorage.getUserById(user1.getId()).getName());
        assertEquals("newLogin1", userStorage.getUserById(user1.getId()).getLogin());
    }

    @Test
    void createUserWithExistLogin() {
        User user1 = new User(1L, "user1@email.com", "user1", "user1",
                LocalDate.of(1997, 01, 26));

        User user2 = new User(2L, "user2@email.com", "user1", "user2",
                LocalDate.of(1997, 01, 26));

        userStorage.create(user1);
        Exception exception = assertThrows(ValidationException.class, () -> {
            userStorage.create(user2);
                });
        assertEquals("Данный логин занят", exception.getMessage());
    }

    @Test
    void createUserWithExistEmail() {
        User user1 = new User(1L, "user1@email.com", "user1", "user1",
                LocalDate.of(1997, 01, 26));
        User user2 = new User(2L, "user1@email.com", "user2", "user2",
                LocalDate.of(1997, 01, 26));

        userStorage.create(user1);
        Exception exception = assertThrows(ValidationException.class, () -> {
            userStorage.create(user2);
        });
        assertEquals("Данный email уже используется", exception.getMessage());
    }

     @Test
     void updateUserWithExistLogin() {
         User user1 = new User(1L, "user1@email.com", "user1", "user1",
                 LocalDate.of(1997, 01, 26));
         User user2 = new User(2L, "user2@email.com", "user2", "user2",
                 LocalDate.of(1997, 01, 26));
         User updateUser1 = new User(1L, "user1new@email.com", "user2", "user1",
                 LocalDate.of(1997, 01, 26));

        userStorage.create(user1);
        userStorage.create(user2);

        Exception exception = assertThrows(ValidationException.class, () -> {
            userStorage.update(updateUser1);
        });
        assertEquals("Этот логин уже используется", exception.getMessage());
     }

    @Test
    void updateUserWithExistEmail() {
        User user1 = new User(1L, "user1@email.com", "user1", "user1",
                LocalDate.of(1997, 01, 26));
        User user2 = new User(2L, "user2@email.com", "user2", "user2",
                LocalDate.of(1997, 01, 26));
        User updateUser1 = new User(1L, "user2@email.com", "user1", "user1",
                LocalDate.of(1997, 01, 26));

        userStorage.create(user1);
        userStorage.create(user2);

        Exception exception = assertThrows(ValidationException.class, () -> {
            userStorage.update(updateUser1);
        });
        assertEquals("Этот e-mail уже используется", exception.getMessage());
    }
}