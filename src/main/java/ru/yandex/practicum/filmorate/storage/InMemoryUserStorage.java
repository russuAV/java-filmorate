package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

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
    public Collection<User> findAll() {
        return users.values();
    }

    @Override
    public User getUserById(Long id) {
        log.debug("Попытка полючить пользователя с id {}.", id);
        User user = users.get(id);
        if (user == null) {
            log.error("Пользователь с id {} не найден.", id);
            throw new NotFoundException("Пользователь с id " + id + " не найден.");
        }
        log.info("Пользователь с id {} получен.", id);
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
        if (!userWithNewData.getEmail().equals(userWithOldData.getEmail()) &&
                usersByEmail.containsKey(userWithNewData.getEmail())) {
            log.error("Ошибка обновления: email {} уже используется", userWithNewData.getEmail());
            throw new ValidationException("Этот e-mail уже используется");
        }

        // обновляем e-mail
        usersByEmail.remove(userWithOldData.getEmail());
        userWithOldData.setEmail(userWithNewData.getEmail());
        usersByEmail.put(userWithOldData.getEmail(), userWithOldData);

        // обновляем логин
        if (!userWithNewData.getLogin().equals(userWithOldData.getLogin()) &&
                usersByLogin.containsKey(userWithNewData.getLogin())) {
            log.error("Ошибка обновления: login {} уже используется", userWithNewData.getLogin());
            throw new ValidationException("Этот логин уже используется");
        }
        usersByLogin.remove(userWithOldData.getLogin());
        userWithOldData.setLogin(userWithNewData.getLogin());
        usersByLogin.put(userWithOldData.getLogin(), userWithOldData);

        // обновляем имя
        userWithOldData.setName(userWithNewData.getName());

        // обновляем дату рождения
        userWithOldData.setBirthday(userWithNewData.getBirthday());
        log.info("Пользователь с id {} успешно обновлён", userWithOldData.getId());
        return userWithOldData;
    }

    @Override
    public long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}