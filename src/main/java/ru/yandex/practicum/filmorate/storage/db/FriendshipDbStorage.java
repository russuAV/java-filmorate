package ru.yandex.practicum.filmorate.storage.db;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FriendshipStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;
import java.util.Optional;

@Repository
@Primary
@RequiredArgsConstructor
public class FriendshipDbStorage implements FriendshipStorage {
    private final JdbcTemplate jdbc;
    private final UserStorage userStorage;

    private static final String INSERT_QUERY = """
            INSERT INTO friendships (sender_id, receiver_id, status, created_at)
            VALUES (?, ?, 'pending', CURRENT_TIMESTAMP)
            """;
    private static final String UPDATE_QUERY = """
            UPDATE friendships SET status = 'confirmed', confirmed_at = CURRENT_TIMESTAMP
            WHERE sender_id = ?
            AND receiver_id = ?
            AND status = 'pending'
            """;
    private static final String FIND_PENDING_QUERY = """
    SELECT EXISTS (
        SELECT 1 FROM friendships
        WHERE sender_id = ?
        AND receiver_id = ?
        AND status = 'pending'
    )
    """;
    private static final String DELETE_FRIEND_QUERY = """
            DELETE FROM friendships
            WHERE sender_id = ?
            AND receiver_id = ?
            """;
    private static final String GET_CONFIRMED_FRIENDS_QUERY = """
            SELECT receiver_id
            FROM friendships
            WHERE sender_id = ?
            AND status = 'confirmed'
            """;
    private static final String GET_UNCONFIRMED_FRIENDS_QUERY = """
            SELECT receiver_id
            FROM friendships
            WHERE sender_id = ?
            AND status = 'pending'
            """;
    private static final String GET_COMMON_FRIENDS_QUERY = """
            SELECT f1.receiver_id
            FROM friendships f1
            JOIN friendships f2 ON f1.receiver_id = f2.receiver_id
            WHERE f1.sender_id = ?
            AND f2.sender_id = ?
            """;

    @Override
    public void sendFriendRequest(Long senderId, Long receivedId) {
        jdbc.update(INSERT_QUERY, senderId, receivedId);
    }

    @Override
    public void confirmFriendRequest(Long senderId, Long receiverId) {
        int updated = jdbc.update(UPDATE_QUERY, senderId, receiverId);
        if (updated == 0) {
            throw new NotFoundException("Заявка не найдена, или уже подтверждена.");
        }
    }

    @Override
    public void deleteFriend(Long requesterId, Long targetId) {
        jdbc.update(DELETE_FRIEND_QUERY, requesterId, targetId);
    }

    @Override
    public boolean existPendingRequest(Long senderId, Long receiverId) {
        Boolean exist = jdbc.queryForObject(FIND_PENDING_QUERY, Boolean.class, senderId, receiverId);
        return Boolean.TRUE.equals(exist);
    }

    @Override
    public List<User> getConfirmedFriends(Long userId) {
        List<Long> friendsIds = jdbc.queryForList(GET_CONFIRMED_FRIENDS_QUERY, Long.class, userId);
        return friendsIds.stream()
                .map(userStorage::getUserById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();
    }

    @Override
    public List<User> getUnconfirmedFriends(Long userId) {
        List<Long> friendsIds = jdbc.queryForList(GET_UNCONFIRMED_FRIENDS_QUERY, Long.class, userId);
        return friendsIds.stream()
                .map(userStorage::getUserById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();
    }

    @Override
    public List<User> getCommonFriends(Long user1Id, Long user2Id) {
        List<Long> commonFriendsIds = jdbc.queryForList(GET_COMMON_FRIENDS_QUERY, Long.class, user1Id, user2Id);
        return commonFriendsIds.stream()
                .map(userStorage::getUserById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();
    }
}