package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.userdata.FriendshipStatus;
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
    void shouldSendAndConfirmFriendRequest() {
        User user1 = new User(1L, "user1@email.com", "user1", "user1",
                LocalDate.of(1997, 01, 26));
        User user2 = new User(2L, "user2@email.com", "user2", "user2",
                LocalDate.of(1997, 01, 27));
        userStorage.create(user1);
        userStorage.create(user2);

        userService.sendFriendRequest(1L, 2L);
        assertTrue(user1.getFriendships().stream()
                .anyMatch(f -> f.getFriendId() == user2.getId()
                && f.getStatus() == FriendshipStatus.PENDING));

        userService.confirmFriendRequest(2L, 1L);
        assertTrue(user2.getFriendships().stream()
                .anyMatch(f -> f.getFriendId() == user1.getId()
                && f.getStatus() == FriendshipStatus.CONFIRMED));
    }

    @Test
    void shouldReturnListOfFriends() {
        User user1 = new User(1L, "user1@email.com", "user1", "user1",
                LocalDate.of(1997, 1, 26));
        User user2 = new User(2L, "user2@email.com", "user2", "user2",
                LocalDate.of(1998, 2, 15));
        userStorage.create(user1);
        userStorage.create(user2);

        userService.sendFriendRequest(1L, 2L);
        userService.confirmFriendRequest(2L, 1L);

        List<User> friends = userService.getFriends(1L);

        // Проверка
        assertEquals(1, friends.size());
        assertTrue(friends.contains(user2));
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

        userService.sendFriendRequest(1L, 3L);
        userService.sendFriendRequest(2L, 3L);
        userService.confirmFriendRequest(3L, 1L);
        userService.confirmFriendRequest(3L, 2L);

        List<User> commonFriends = userService.getCommonFriends(1L, 2L);
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

        userService.sendFriendRequest(1L, 2L);
        userService.confirmFriendRequest(2L, 1L);

        assertTrue(user1.getFriendships().stream()
                .anyMatch(f -> f.getFriendId() == user2.getId()
                        && f.getStatus() == FriendshipStatus.CONFIRMED));
        userService.deleteFriend(user1.getId(), user2.getId());

        assertFalse(user1.getFriendships().stream()
                .anyMatch(f -> f.getFriendId() == user2.getId()
                        && f.getStatus() == FriendshipStatus.CONFIRMED));
        assertFalse(user1.getFriendships().stream()
                .anyMatch(f -> f.getFriendId() == user2.getId()
                        && f.getStatus() == FriendshipStatus.PENDING));

    }
}