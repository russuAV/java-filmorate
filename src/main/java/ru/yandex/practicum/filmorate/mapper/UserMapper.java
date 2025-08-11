package ru.yandex.practicum.filmorate.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.dto.UpdateUserRequest;
import ru.yandex.practicum.filmorate.dto.UserDto;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.dto.NewUserRequest;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class UserMapper {

    public static UserDto mapToUserDto(User user) {
       UserDto userDto = new UserDto();
       userDto.setId(user.getId());
       userDto.setLogin(user.getLogin());
       userDto.setName(user.getName());
       userDto.setBirthday(user.getBirthday());
       userDto.setEmail(user.getEmail());

       return userDto;
    }

    public static User mapToUser(NewUserRequest request) {
        User user = new User();
        user.setEmail(request.getEmail());
        user.setLogin(request.getLogin());
        user.setName(request.getName());
        user.setBirthday(request.getBirthday());

        return user;
    }

    public static User updateUserFields(User user, UpdateUserRequest request) {
        if (request.hasId()) {
            user.setId(request.getId());
        }
        if (request.hasEmail()) {
            user.setEmail(request.getEmail());
        }
        if (request.hasLogin()) {
            user.setLogin(request.getLogin());
        }
        if (request.hasName()) {
            user.setName(request.getName());
        }
        if (request.hasBirthday()) {
            user.setBirthday(request.getBirthday());
        }

        return user;
    }
}