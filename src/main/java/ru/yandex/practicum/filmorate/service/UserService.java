package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.userdata.Friendship;
import ru.yandex.practicum.filmorate.model.userdata.FriendshipStatus;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public void sendFriendRequest(Long senderId, Long receivedId) {
        log.debug("Попытка пользователя {} отправить запрос в друзья пользователю {}", senderId, receivedId);
        User[] users = validateAndGetUsers(senderId, receivedId);
        User sender = users[0];
        User receiver = users[1];

        Friendship friendshipRequest = new Friendship(senderId, receivedId, FriendshipStatus.PENDING);
        if (sender.getFriendships().contains(friendshipRequest)) {
            throw new ValidationException("Заявка уже была отправлена ранее.");
        }
        sender.getFriendships().add(friendshipRequest);
        log.info("Заявка в друзья от пользователя {} к {} отправлена.", senderId, receivedId);
    }

    public void confirmFriendRequest(Long receiverId, Long senderId) {
        log.debug("Попытка пользователя {} добавить в друзья пользователя {}", receiverId, senderId);
        User[] users = validateAndGetUsers(receiverId, senderId);
        User receiver = users[0];
        User sender = users[1];

        Optional<Friendship> friendshipOpt = sender.getFriendships().stream()
                .filter(f -> f.getUserId() == senderId && f.getFriendId() == receiverId
                        && f.getStatus() == FriendshipStatus.PENDING)
                .findFirst();

        if (friendshipOpt.isEmpty()) {
            throw new ValidationException("Заявка на добавление в друзья не найдена.");
        }

        Friendship friendship = friendshipOpt.get();

        if (friendship.getStatus() == FriendshipStatus.CONFIRMED) {
            throw new ValidationException("Заявка уже подтверждена ранее.");
        }

        friendship.setStatus(FriendshipStatus.CONFIRMED);

        // Добавляем в список получателя
        receiver.getFriendships().add(new Friendship(receiverId, senderId, FriendshipStatus.CONFIRMED));
        log.info("Заявка в друзья от пользователя {} к {} принята.", receiverId, senderId);
    }


    public List<User> getFriends(Long id) {
        log.debug("Попытка получить список друзей пользователя с id {}", id);
        User user = userStorage.getUserById(id);

        List<User> friendsList = user.getFriendships()
                .stream()
                .filter(friendship -> friendship.getStatus() == FriendshipStatus.CONFIRMED)
                .map(Friendship::getFriendId)
                .map(userStorage::getUserById)
                .toList();
        log.info("Получен список друзей пользователя с id {}", id);

        return friendsList;
    }

    public List<User> getCommonFriends(Long user1Id, Long user2Id) {
        log.debug("Запрос на получение списка общих друзей");
        User[] users = validateAndGetUsers(user1Id, user2Id);
        User user1 = users[0];
        User user2 = users[1];

        // Получаем confirmed-друзей первого пользователя
        Set<Long> user1Friends = user1.getFriendships().stream()
                .filter(f -> f.getStatus() == FriendshipStatus.CONFIRMED)
                .map(Friendship::getFriendId)
                .collect(Collectors.toSet());

        // Получаем confirmed-друзей второго пользователя
        Set<Long> user2Friends = user2.getFriendships().stream()
                .filter(f -> f.getStatus() == FriendshipStatus.CONFIRMED)
                .map(Friendship::getFriendId)
                .collect(Collectors.toSet());
        // Пересечение
        user1Friends.retainAll(user2Friends);

        log.info("Выведен список общих друзей. Найдено общих друзей {}", user1Friends.size());

        return user1Friends.stream()
                .map(userStorage::getUserById)
                .toList();
    }

    public void deleteFriend(Long requesterId, Long targetId) {
        log.debug("Попытка пользователя {} удалить из друзей пользователя {}", requesterId, targetId);
        User[] users = validateAndGetUsers(requesterId, targetId);
        User user = users[0];
        User friend = users[1];

        boolean removed1 = user.getFriendships().removeIf(f -> f.getFriendId() == targetId
                && f.getStatus() == FriendshipStatus.CONFIRMED);

        boolean removed2 = friend.getFriendships().removeIf(f -> f.getFriendId() == requesterId
                && f.getStatus() == FriendshipStatus.CONFIRMED);
        if (!removed1 && !removed2) {
            log.warn("Пользователь с id {} отсутствует в списке друзей у пользователя {}", targetId, requesterId);
            return;
        }

        log.info("Пользователь {} успешно удалил из друзей пользователя {}", requesterId, targetId);
    }

    private User[] validateAndGetUsers(Long user1Id, Long user2Id) {
        log.trace("Валидация существования пользователей с id {} и {}", user1Id, user2Id);
        if (user1Id == null || user2Id == null) {
            log.error("Отсутствует информация об ID");
            throw new ConditionsNotMetException("Id должен быть указан");
        }
        if (user1Id.equals(user2Id)) {
            log.error("ID совпадают, неверно указаны идентификаторы пользователей");
            throw new ValidationException("ID совпадают. Неверный ввод данных");
        }

        User user1 = userStorage.getUserById(user1Id);
        User user2 = userStorage.getUserById(user2Id);

        log.trace("Валидация пользователей с ID {} и {} прошла успешно", user1Id, user2Id);
        return new User[]{user1, user2};
    }
}