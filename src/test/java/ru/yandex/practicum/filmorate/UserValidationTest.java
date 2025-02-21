package ru.yandex.practicum.filmorate;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class UserValidationTest {

    private Validator validator;

    @BeforeEach
    public void setup() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void testValidUser() {
        // Создаем корректного пользователя
        User user = new User(null, "user@example.com", "userlogin", "User Name",
                LocalDate.of(1990, 1, 1));
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertTrue(violations.isEmpty(), "Валидация должна пройти без ошибок для корректного пользователя");
    }

    @Test
    public void testInvalidEmail() {
        // Email не соответствует формату
        User user = new User(null, "invalid-email", "userlogin", "User Name",
                LocalDate.of(1990, 1, 1));
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty(), "Валидация должна выдать ошибку для некорректного email");
    }

    @Test
    public void testEmailCannotBeBlank() {
        User user = new User(null, "", "UserLogin", "User Name",
                LocalDate.of(1990, 1, 1));
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty(), "Email не может быть пустым.");
    }

    @Test
    public void testBlankLogin() {
        // Логин пустой
        User user = new User(null, "user@example.com", "", "User Name",
                LocalDate.of(1990, 1, 1));
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty(), "Валидация должна выдать ошибку для пустого логина");
    }

    @Test
    public void testLoginCannotContainSpaces() {
        User user = new User(null, "user@example.com", "User Login", "User Name",
                LocalDate.of(1990, 1, 1));
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty(), "Логин не должен содержать пробелы.");
    }

    @Test
    public void testNameDefaultsToLogin() {
        User user = new User(null, "user@example.com", "UserLogin", "",
                LocalDate.of(1990, 1, 1));
        assertEquals("UserLogin", user.getName(),
                "Если name пустой, должно использоваться значение login.");
    }

    @Test
    public void testFutureBirthday() {
        // Дата рождения в будущем
        User user = new User(null, "user@example.com", "userlogin", "User Name",
                LocalDate.now().plusDays(1));
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty(), "Валидация должна выдать ошибку для даты рождения в будущем");
    }

    @Test
    public void testEmptyUser1() {
        User user = new User();
        Set<ConstraintViolation<User>> violations = validator.validate(user);

        // Проверяем, что минимум 3 ошибки: нет email, login и birthday
        assertEquals(3, violations.size(), "Должно быть минимум 3 ошибки (email, login, birthday)");
    }
}