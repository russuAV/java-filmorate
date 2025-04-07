package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface FriendshipStorage {

    void sendFriendRequest(Long senderId, Long receivedId);

    void confirmFriendRequest(Long senderId, Long receiverId);

    boolean existPendingRequest(Long senderId, Long receiverId);

    List<User> getConfirmedFriends(Long userId);

    List<User> getUnconfirmedFriends(Long userId);

    List<User> getCommonFriends(Long user1Id, Long user2Id);

    void deleteFriend(Long requesterId, Long targetId);
}