package ru.yandex.practicum.filmorate.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class NewUserRequest {
    @Email(message = "Некорректный email")
    @NotBlank(message = "email обязателен")
    private String email;

    @NotBlank(message = "Логин обязателен")
    private String login;
    private String name;

    @Past(message = "Дата рождения не может быть позже, чем сегодня")
    private LocalDate birthday;
}