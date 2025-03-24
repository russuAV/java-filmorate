package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserServiceTest {
    private UserService userService;
    private UserStorage userStorage;

    @BeforeEach
    void setup() {
        userStorage = new InMemoryUserStorage() {
        };
        userService = new UserService(userStorage);
    }

    @Test
    void shouldAddFriend() {
        User user1 = new User(1L, "user1@email.com", "user1", "user1",
                LocalDate.of(1997, 01, 26));
        User user2 = new User(2L, "user2@email.com", "user2", "user2",
                LocalDate.of(1997, 01, 27));
        userStorage.create(user1);
        userStorage.create(user2);

        userService.addFriend(1L, 2L);
        assertTrue(user1.getFriends().contains(2L));
        assertTrue(user2.getFriends().contains(1L));
    }

    @Test
    void shouldGetFriends() {
        User user1 = new User(1L, "user1@email.com", "user1", "user1",
                LocalDate.of(1997, 01, 26));
        userStorage.create(user1);
        User theSameUser = userStorage.getUserById(1L);
        assertEquals(user1, theSameUser);
    }

    @Test
    void getCommonFriends() {
        User user1 = new User(1L, "user1@email.com", "user1", "user1",
                LocalDate.of(1997, 01, 26));
        User user2 = new User(2L, "user2@email.com", "user2", "user2",
                LocalDate.of(1997, 01, 27));
        User user3 = new User(3L, "user3@email.com", "user3", "user3",
                LocalDate.of(1997, 01, 28));
        userStorage.create(user1);
        userStorage.create(user2);
        userStorage.create(user3);

        userService.addFriend(user1.getId(), user3.getId());
        userService.addFriend(user2.getId(), user3.getId());

        List<User> commonFriends = userService.getCommonFriends(user1.getId(), user2.getId());
        assertTrue(commonFriends.contains(user3));
    }

    @Test
    void deleteFriend() {
        User user1 = new User(1L, "user1@email.com", "user1", "user1",
                LocalDate.of(1997, 01, 26));
        User user2 = new User(2L, "user2@email.com", "user2", "user2",
                LocalDate.of(1997, 01, 27));

        userStorage.create(user1);
        userStorage.create(user2);

        userService.addFriend(user1.getId(), user2.getId());
        userService.deleteFriend(user1.getId(), user2.getId());

        assertFalse(user1.getFriends().contains(user2.getId()));
        assertFalse(user2.getFriends().contains(user1.getId()));

    }
}