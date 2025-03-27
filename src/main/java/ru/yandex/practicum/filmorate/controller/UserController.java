package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
private final UserStorage userStorage;
private final UserService userService;

    public UserController(UserStorage userStorage, UserService userService) {
        this.userStorage = userStorage;
        this.userService = userService;
    }

    @GetMapping
    public Collection<User> findAll() {
        return userStorage.findAll();
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable Long id) {
        return userStorage.getUserById(id);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void sendFriendRequest(@PathVariable("id") Long senderId,
                                  @PathVariable("friendId") Long receiverId) {
        userService.sendFriendRequest(senderId, receiverId);
    }

    @PutMapping("/{id}/friends/{friendId}/confirm")
    public void confirmFriendRequest(@PathVariable("id") Long receiverId,
                                     @PathVariable("friendId") Long senderId) {
        userService.confirmFriendRequest(receiverId, senderId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void deleteFriend(@PathVariable("id") Long requesterId,
                              @PathVariable("friendId") Long targetId) {
        userService.deleteFriend(requesterId, targetId);
    }

    @GetMapping("/{id}/friends")
    public List<User> getUserFriends(@PathVariable Long id) {
        return userService.getFriends(id);
    }

    @GetMapping("/{id}/friends/common/{friendId}")
    public List<User> getCommonFriends(@PathVariable("id") Long user1Id,
                                       @PathVariable("friendId") Long user2Id) {
        return userService.getCommonFriends(user1Id, user2Id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public User create(@Valid @RequestBody User user) {
        return userStorage.create(user);
    }

    @PutMapping
    public User update(@Valid @RequestBody User userWithNewData) {
        return userStorage.update(userWithNewData);
    }
}