package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FriendshipStorage;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FriendshipService {
    private final FriendshipStorage friendshipStorage;
    private final UserService userService;

    public void sendFriendRequest(Long senderId, Long receivedId) {
        log.debug("Попытка пользователя {} отправить запрос в друзья пользователю {}", senderId, receivedId);
        validateAndGetUsers(senderId, receivedId);

        if (friendshipStorage.existPendingRequest(senderId, receivedId)) {
            throw new ValidationException("Заявка уже была отправлена ранее.");
        }

        friendshipStorage.sendFriendRequest(senderId, receivedId);
        log.info("Заявка в друзья от пользователя {} к {} отправлена.", senderId, receivedId);
    }

    public void confirmFriendRequest(Long receiverId, Long senderId) {
        log.debug("Попытка пользователя {} добавить в друзья пользователя {}", receiverId, senderId);
        validateAndGetUsers(receiverId, senderId);

        if (!friendshipStorage.existPendingRequest(senderId, receiverId)) {
            throw new NotFoundException("Заявка на добавление в друзья не найдена.");
        }

        List<User> friends = friendshipStorage.getConfirmedFriends(senderId);
        boolean alreadyConfirmed = friends.stream()
                .anyMatch(friend -> friend.getId().equals(receiverId));
        if (alreadyConfirmed) {
            throw new ValidationException("Заявка уже подтверждена ранее.");
        }

        friendshipStorage.confirmFriendRequest(senderId, receiverId);
        log.info("Заявка в друзья от пользователя {} к {} принята.", receiverId, senderId);
    }

    public List<User> getConfirmedFriends(Long userId) {
        log.debug("Попытка получить список подтвержденных друзей пользователя с id {}", userId);
        userService.getUserById(userId);
        List<User> friendsList = friendshipStorage.getConfirmedFriends(userId);

        log.info("Получен список подтвержденных друзей пользователя с id {}", userId);
        return friendsList;
    }

    public List<User> getUnconfirmedFriends(Long userId) {
        log.debug("Попытка получить список неподтвержденных друзей пользователя с id {}", userId);
        userService.getUserById(userId);
        List<User> friendsList = friendshipStorage.getUnconfirmedFriends(userId);

        log.info("Получен список неподтвержденных друзей пользователя с id {}", userId);
        return friendsList;
    }

    public List<User> getCommonFriends(Long user1Id, Long user2Id) {
        log.debug("Запрос на получение списка общих друзей");
        validateAndGetUsers(user1Id, user2Id);
        List<User> commonFriends = friendshipStorage.getCommonFriends(user1Id, user2Id);

        log.info("Выведен список общих друзей. Найдено общих друзей {}", commonFriends.size());
        return commonFriends;
    }

    public void deleteFriend(Long requesterId, Long targetId) {
        log.debug("Попытка пользователя {} удалить из друзей пользователя {}", requesterId, targetId);
        validateAndGetUsers(requesterId, targetId);

        List<User> friends = friendshipStorage.getUnconfirmedFriends(requesterId);
        boolean isFriend = friends.stream()
                .anyMatch(friend -> friend.getId().equals(targetId));
        if (!isFriend) {
            log.warn("Пользователь с id {} отсутствует в списке друзей у пользователя {}", targetId, requesterId);
        }
        friendshipStorage.deleteFriend(requesterId, targetId);
        log.info("Пользователь {} успешно удалил из друзей пользователя {}", requesterId, targetId);
    }

    private void validateAndGetUsers(Long user1Id, Long user2Id) {
        log.trace("Валидация существования пользователей с id {} и {}", user1Id, user2Id);
        if (user1Id == null || user2Id == null) {
            log.error("Отсутствует информация об ID");
            throw new ConditionsNotMetException("Id должен быть указан");
        }
        if (user1Id.equals(user2Id)) {
            log.error("ID совпадают, неверно указаны идентификаторы пользователей");
            throw new ValidationException("ID совпадают. Неверный ввод данных");
        }

        userService.getUserById(user1Id);
        userService.getUserById(user2Id);

        log.trace("Валидация пользователей с ID {} и {} прошла успешно", user1Id, user2Id);
    }
}