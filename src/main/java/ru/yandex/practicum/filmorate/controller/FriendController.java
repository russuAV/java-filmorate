package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.UserDto;
import ru.yandex.practicum.filmorate.mapper.UserMapper;
import ru.yandex.practicum.filmorate.service.FriendshipService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class FriendController {
    private final FriendshipService friendshipService;

    @PutMapping("/{id}/friends/{friendId}")
    public void sendFriendRequest(@PathVariable("id") Long senderId,
                                  @PathVariable("friendId") Long receiverId) {
        friendshipService.sendFriendRequest(senderId, receiverId);
    }

    @PutMapping("/{id}/friends/{friendId}/confirm")
    public void confirmFriendRequest(@PathVariable("id") Long senderId,
                                     @PathVariable("friendId") Long receiverId) {
        friendshipService.confirmFriendRequest(senderId, receiverId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void deleteFriend(@PathVariable("id") Long requesterId,
                             @PathVariable("friendId") Long targetId) {
        friendshipService.deleteFriend(requesterId, targetId);
    }

    @GetMapping("/{id}/friends/confirmed")
    public List<UserDto> getConfirmedFriends(@PathVariable("id") Long userId) {
        return friendshipService.getConfirmedFriends(userId).stream()
                .map(UserMapper::mapToUserDto)
                .toList();
    }

    @GetMapping("/{id}/friends")
    public List<UserDto> getUnconfirmedFriends(@PathVariable("id") Long userId) {
        return friendshipService.getUnconfirmedFriends(userId).stream()
                .map(UserMapper::mapToUserDto)
                .toList();
    }

    @GetMapping("/{id}/friends/common/{friendId}")
    public List<UserDto> getCommonFriends(@PathVariable("id") Long user1Id,
                                       @PathVariable("friendId") Long user2Id) {
        return friendshipService.getCommonFriends(user1Id, user2Id).stream()
                .map(UserMapper::mapToUserDto)
                .toList();
    }
}