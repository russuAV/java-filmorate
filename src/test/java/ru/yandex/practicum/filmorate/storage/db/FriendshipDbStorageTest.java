package ru.yandex.practicum.filmorate.storage.db;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.mapper.UserRowMapper;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@Import({FriendshipDbStorage.class, UserDbStorage.class, UserRowMapper.class})
class FriendshipDbStorageTest {

    @Autowired
    private FriendshipDbStorage friendshipDbStorage;

    @Autowired
    private UserDbStorage userDbStorage;

    private User createUser(String login, String email) {
        User user = new User();
        user.setLogin(login);
        user.setEmail(email);
        user.setName(login);
        user.setBirthday(LocalDate.of(2000, 1, 1));
        return userDbStorage.create(user);
    }

    @Test
    void testAddAndGetFriends() {
        User user1 = createUser("user1", "user1@mail.com");
        User user2 = createUser("user2", "user2@mail.com");

        friendshipDbStorage.sendFriendRequest(user1.getId(), user2.getId());

        List<User> friends = friendshipDbStorage.getUnconfirmedFriends(user1.getId());
        assertThat(friends).hasSize(1).contains(user2);
    }

    @Test
    void testGetCommonFriends() {
        User user1 = createUser("user1", "user1@mail.com");
        User user2 = createUser("user2", "user2@mail.com");
        User user3 = createUser("user3", "user3@mail.com");

        friendshipDbStorage.sendFriendRequest(user1.getId(), user3.getId());
        friendshipDbStorage.sendFriendRequest(user2.getId(), user3.getId());

        List<User> commonFriends = friendshipDbStorage.getCommonFriends(user1.getId(), user2.getId());

        assertThat(commonFriends).hasSize(1).contains(user3);
    }

    @Test
    void testDeleteFriend() {
        User user1 = createUser("user1", "user1@mail.com");
        User user2 = createUser("user2", "user2@mail.com");

        friendshipDbStorage.sendFriendRequest(user1.getId(), user2.getId());
        friendshipDbStorage.deleteFriend(user1.getId(), user2.getId());

        List<User> friends = friendshipDbStorage.getUnconfirmedFriends(user1.getId());
        assertThat(friends).isEmpty();
    }
}