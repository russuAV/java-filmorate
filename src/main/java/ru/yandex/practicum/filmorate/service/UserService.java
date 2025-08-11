package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.NewUserRequest;
import ru.yandex.practicum.filmorate.dto.UpdateUserRequest;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.UserMapper;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class  UserService {
    private final UserStorage userStorage;

    public User create(NewUserRequest request) {
        log.debug("Создание пользователя: {}", request);
        User user = UserMapper.mapToUser(request);

        log.info("Пользователь успешно создан: {}", request);
        return userStorage.create(user);
    }

    public User update(UpdateUserRequest request) {
        log.debug("Обновление пользователя с id = {}", request.getId());
        User existing = userStorage.getUserById(request.getId())
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + request.getId() + " не найден."));
        User updated = UserMapper.updateUserFields(existing, request);

        log.info("Пользователь с id = {} успешно обновлен", updated.getId());
        return userStorage.update(updated);
    }

    public void delete(Long userId) {
        log.debug("Удаление пользователя с id = {}", userId);
        userStorage.getUserById(userId);
        userStorage.delete(userId);
        log.info("Пользователь с id = {} успешно удалён", userId);
    }

    public User getUserById(Long userId) {
        log.debug("Попытка получить пользователя с id = {}", userId);
        User user = userStorage.getUserById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден."));

        log.info("Пользователь с id = {} успешно получен", userId);
        return user;
    }

    public User getUserByEmail(String email) {
        log.debug("Попытка получить пользователя с email = {}", email);
        User user = userStorage.getUserByEmail(email)
                .orElseThrow(() -> new NotFoundException("Пользователь с email " + email + " не найден."));

        log.info("Пользователь с email = {} успешно получен", email);
        return user;
    }

    public List<User> findAll() {
        log.debug("Попытка получить список всех пользователей");
        List<User> users = userStorage.findAll();

        log.info("Список всех пользователей успешно получен");
        return users;
    }
}