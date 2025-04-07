package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;

public interface UserStorage {

    List<User> findAll();

    Optional<User> getUserById(Long userId);

    Optional<User> getUserByEmail(String email);

    User create(User user);

    User update(User userWithNewData);

    void delete(Long userId);
}