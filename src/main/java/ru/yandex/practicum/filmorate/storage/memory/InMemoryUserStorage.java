package ru.yandex.practicum.filmorate.storage.memory;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {
    // для пользователей по id
    private final Map<Long, User> users = new HashMap<>();
    // для быстрой проверки уникальности email
    private final Map<String, User> usersByEmail = new HashMap<>();
    // для быстрой проверки уникальности login
    private final Map<String, User> usersByLogin = new HashMap<>();

    @Override
    public List<User> findAll() {
        return users.values().stream()
                .toList();
    }

    @Override
    public Optional<User> getUserById(Long userId) {
        log.debug("Попытка полючить пользователя с id {}.", userId);
        Optional<User> user = Optional.ofNullable(users.get(userId));
        if (user.isEmpty()) {
            log.error("Пользователь с id {} не найден.", userId);
            throw new NotFoundException("Пользователь с id " + userId + " не найден.");
        }
        log.info("Пользователь с id {} получен.", userId);
        return user;
    }

    @Override
    public Optional<User> getUserByEmail(String email) {
        log.debug("Попытка полючить пользователя с email {}.", email);
        Optional<User> user = Optional.ofNullable(usersByEmail.get(email));
        if (user.isEmpty()) {
            log.error("Пользователь с email {} не найден.", email);
            throw new NotFoundException("Пользователь с email " + email + " не найден.");
        }
        log.info("Пользователь с email {} получен.", email);
        return user;
    }

    @Override
    public User create(User user) {
        log.info("Попытка создания пользователя с email: {} и login: {}", user.getEmail(), user.getLogin());
        if (users.containsKey(user.getId())) {
            log.error("Ошибка создания пользователя: id {} уже используется", user.getId());
            throw new ValidationException("Пользователь уже зарегистрирован");
        }
        if (usersByEmail.containsKey(user.getEmail())) {
            log.error("Ошибка создания пользователя: email {} уже используется", user.getEmail());
            throw new ValidationException("Данный email уже используется");
        }
        if (usersByLogin.containsKey(user.getLogin())) {
            log.error("Ошибка создания пользователя: login {} уже используется", user.getLogin());
            throw new ValidationException("Данный логин занят");
        }
        user.setId(getNextId());
        users.put(user.getId(), user);
        usersByEmail.put(user.getEmail(), user);
        usersByLogin.put(user.getLogin(), user);

        log.info("Пользователь успешно создан с id: {}, email: {}, login: {}",
                user.getId(), user.getEmail(), user.getLogin());
        return user;
    }

    @Override
    public User update(User userWithNewData) {
        log.info("Попытка обновления пользователя с id: {}", userWithNewData.getId());

        if (userWithNewData.getId() == null || userWithNewData.getId() == 0) {
            throw new ValidationException("id должен быть указан");
        }
        User userWithOldData = users.get(userWithNewData.getId());
        if (userWithOldData == null) {
            log.error("Пользователь не найден");
            throw new NotFoundException("Пользователь не найден");
        }
        // проверяем доступность email
        if (!userWithNewData.getEmail().equals(userWithOldData.getEmail())) {
            if (usersByEmail.containsKey(userWithNewData.getEmail())) {
                User existingUserByEmail = usersByEmail.get(userWithNewData.getEmail());
                if (!existingUserByEmail.getId().equals(userWithNewData.getId())) {
                    log.error("Ошибка обновления: email {} уже используется", userWithNewData.getEmail());
                    throw new ValidationException("Этот e-mail уже используется");
                }
            }
        }

        // проверяем доступность логина
        if (!userWithNewData.getLogin().equals(userWithOldData.getLogin())) {
            if (usersByLogin.containsKey(userWithNewData.getLogin())) {
                User existingUserByLogin = usersByLogin.get(userWithNewData.getLogin());
                if (!existingUserByLogin.getId().equals(userWithNewData.getId())) {
                    log.error("Ошибка обновления: login {} уже используется", userWithNewData.getLogin());
                    throw new ValidationException("Этот логин уже используется");
                }
            }
        }

        User updateUser = new User(
                userWithNewData.getId(),
                userWithNewData.getEmail(),
                userWithNewData.getLogin(),
                userWithNewData.getName(),
                userWithNewData.getBirthday()
        );

        updateUser.setFriendships(new HashSet<>(userWithOldData.getFriendships()));

        // удаляем старого пользователя и возвращаем нового
        users.remove(userWithOldData.getId());
        usersByEmail.remove(userWithOldData.getEmail());
        usersByLogin.remove(userWithOldData.getLogin());
        users.put(userWithNewData.getId(), userWithNewData);
        usersByLogin.put(userWithNewData.getLogin(), userWithNewData);
        usersByEmail.put(userWithNewData.getEmail(), userWithNewData);

        log.info("Пользователь с id {} успешно обновлён", userWithOldData.getId());
        return updateUser;
    }

    @Override
    public void delete(Long userId) {
        log.debug("Попытка удалить пользователя с id {}", userId);
        getUserById(userId);
        users.remove(userId);
        log.info("Пользователь с id {} удален", userId);
    }

    public long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}