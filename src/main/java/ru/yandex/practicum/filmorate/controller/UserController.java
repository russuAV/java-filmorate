package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    // для пользователей по id
    private final Map<Long, User> users = new HashMap<>();
    // для быстрой проверки уникальности email
    private final Map<String, User> usersByEmail = new HashMap<>();
    // для быстрой проверки уникальности login
    private final Map<String, User> usersByLogin = new HashMap<>();


    @GetMapping
    public Collection<User> findAll() {
        return users.values();
    }

    @PostMapping
    public User create(@Valid @RequestBody User user) {
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

    @PutMapping
    public User update(@Valid @RequestBody User userWithNewData) {
        if (userWithNewData.getId() == null || userWithNewData.getId() == 0) {
            throw new ValidationException("id должен быть указан");
        }
        log.info("Попытка обновления пользователя с id: {}", userWithNewData.getId());

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
        usersByLogin.put(userWithOldData.getLogin(),userWithOldData);

        // обновляем имя
        userWithOldData.setName(userWithNewData.getName());

        // обновляем дату рождения
        userWithOldData.setBirthday(userWithNewData.getBirthday());
        log.info("Пользователь с id {} успешно обновлён", userWithOldData.getId());
        return userWithOldData;
    }

    private long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
