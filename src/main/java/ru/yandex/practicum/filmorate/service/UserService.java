package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
public class UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public void addFriend(Long id, Long friendId) {
        log.debug("Попытка пользователя {} добавить в друзья пользователя {}", id, friendId);
        User[] users = validateAndGetUsers(id, friendId);
        User user1 = users[0];
        User user2 = users[1];

        if (user1.getFriends().contains(friendId)) {
            log.error("Пользователь с id {} уже имеется в друзьях у пользователя {}", friendId, id);
            throw new ValidationException("Данный друг уже имеется в списке друзей!");
        }
        user1.getFriends().add(friendId);
        user2.getFriends().add(id);

        log.info("Пользователь {} успешно добавил в друзья пользователя {}", id, friendId);
    }

    public List<User> getFriends(Long id) {
        log.debug("Попытка получить список друзей пользователя с id {}", id);
        User user = userStorage.getUserById(id);
        Set<Long> friendIds = user.getFriends();

        log.info("Получен список друзей пользователя с id {}", id);
        return friendIds.stream()
                .map(userStorage::getUserById)
                .toList();
    }

    public List<User> getCommonFriends(Long id, Long friendsId) {
        log.debug("Запрос на получение списка общих друзей");
        User[] users = validateAndGetUsers(id, friendsId);
        User user1 = users[0];
        User user2 = users[1];

        Set<Long> friendIdsUser1 = user1.getFriends();
        Set<Long> friendsIdsUser2 = user2.getFriends();

        Set<Long> commonFriends = new HashSet<>(friendIdsUser1);
        commonFriends.retainAll(friendsIdsUser2);

        log.info("Выведен список общих друзей");
        return commonFriends.stream()
                .map(userStorage::getUserById)
                .toList();
    }

    public void deleteFriend(Long id, Long friendsId) {
        log.debug("Попытка пользователя {} удалить из друзей пользователя {}", id, friendsId);
        User[] users = validateAndGetUsers(id, friendsId);
        User user1 = users[0];
        User user2 = users[1];

        if (!user1.getFriends().contains(friendsId)) {
            log.warn("Пользователь с id {} отсутствует в списке друзей у пользователя {}", friendsId, id);
            return;
        }

        user1.getFriends().remove(friendsId);
        user2.getFriends().remove(id);

        log.info("Пользователь {} успешно удалил из друзей пользователя {}", id, friendsId);
    }

    private User[] validateAndGetUsers(Long id, Long friendsId) {
        log.trace("Валидация существования пользователей с id {} и {}", id, friendsId);
        if (id == null || friendsId == null) {
            log.error("Отсутствует информация об ID");
            throw new ConditionsNotMetException("Id должен быть указан");
        }
        if (id.equals(friendsId)) {
            log.error("ID совпадают, неверно указаны идентификаторы пользователей");
            throw new ValidationException("Личный id, и id друга совпадают. Неверный ввод данных");
        }

        User user1 = userStorage.getUserById(id);
        User user2 = userStorage.getUserById(friendsId);

        log.trace("Валидация пользователей с ID {} и {} прошла успешно", id, friendsId);
        return new User[]{user1, user2};
    }
}