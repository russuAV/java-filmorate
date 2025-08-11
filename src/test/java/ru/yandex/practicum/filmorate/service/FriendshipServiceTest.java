package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;

import ru.yandex.practicum.filmorate.dto.NewUserRequest;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;


import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@AutoConfigureTestDatabase
public class FriendshipServiceTest {

    @Autowired
    private FriendshipService friendshipService;

    @Autowired
    private UserService userService;

    private User user1;
    private User user2;

    @BeforeEach
    void setUp() {
        user1 = userService.create(new NewUserRequest("user1@mail.com", "user1", "User One",
                LocalDate.of(1990, 1, 1)));
        user2 = userService.create(new NewUserRequest("user2@mail.com", "user2", "User Two",
                LocalDate.of(1991, 2, 2)));
    }

    @Test
    void shouldSendFriendRequest() {
        friendshipService.sendFriendRequest(user1.getId(), user2.getId());

        List<User> user1Friends = friendshipService.getUnconfirmedFriends(user1.getId());
        assertThat(user1Friends).hasSize(1).containsExactly(user2);

        List<User> user2Friends = friendshipService.getUnconfirmedFriends(user2.getId());
        assertThat(user2Friends).isEmpty(); // односторонняя дружба
    }

    @Test
    void shouldRemoveFriend() {
        friendshipService.sendFriendRequest(user1.getId(), user2.getId());
        List<User> user1Friends = friendshipService.getUnconfirmedFriends(user1.getId());
        assertThat(user1Friends).hasSize(1).containsExactly(user2);

        friendshipService.deleteFriend(user1.getId(), user2.getId());

        List<User> friends = friendshipService.getUnconfirmedFriends(user1.getId());
        assertThat(friends).isEmpty();
    }

    @Test
    void shouldReturnCommonFriends() {
        User user3 = userService.create(new NewUserRequest("user3@mail.com", "user3", "User Three",
                LocalDate.of(1992, 3, 3)));

        friendshipService.sendFriendRequest(user1.getId(), user3.getId());
        friendshipService.sendFriendRequest(user2.getId(), user3.getId());

        List<User> commonFriends = friendshipService.getCommonFriends(user1.getId(), user2.getId());
        assertThat(commonFriends).hasSize(1).containsExactly(user3);
    }

    @Test
    void shouldThrowWhenAddingFriendForUnknownUser() {
        assertThrows(NotFoundException.class, () -> friendshipService.sendFriendRequest(999L, 1L));
    }

    @Test
    void shouldThrowWhenRemovingNonexistentFriendship() {
        assertThrows(NotFoundException.class, () -> friendshipService.deleteFriend(1L, 999L));
    }
}