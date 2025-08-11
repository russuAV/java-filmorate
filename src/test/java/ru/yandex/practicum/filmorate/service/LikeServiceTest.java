package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.dto.NewFilmRequest;
import ru.yandex.practicum.filmorate.dto.NewUserRequest;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.filmdata.Genre;
import ru.yandex.practicum.filmorate.model.filmdata.MpaRating;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
class LikeServiceTest {

    @Autowired
    private LikeService likeService;

    @Autowired
    private FilmService filmService;

    @Autowired
    private UserService userService;

    @Autowired
    private JdbcTemplate jdbc;

    private Film film;
    private User user1;
    private User user2;

    @BeforeEach
    void setUp() {
        user1 = userService.create(new NewUserRequest("test1@mail.com", "testUser1", "Test User1",
                LocalDate.of(1990, 1, 1)));
        user2 = userService.create(new NewUserRequest("test2@mail.com", "testUser2", "Test User2",
                LocalDate.of(1990, 1, 1)));
        film = filmService.create(new NewFilmRequest("Test Film", "A description",
                LocalDate.of(2000, 1, 1), 100, new MpaRating(1L, ""),
                List.of(new Genre(1L, ""))));
    }

    @Test
    void shouldAddLikeToFilm() {
        likeService.addLike(film.getId(), user1.getId());
        likeService.addLike(film.getId(), user2.getId());

        List<Long> userIdsWhoLikedFilm = likeService.getUserIdsWhoLikedFilm(film.getId());

        assertTrue(userIdsWhoLikedFilm.contains(user1.getId()));
        assertTrue(userIdsWhoLikedFilm.contains(user2.getId()));
    }

    @Test
    void shouldRemoveLikeFromFilm() {
        likeService.addLike(film.getId(), user1.getId());
        likeService.deleteLike(film.getId(), user1.getId());

        List<Long> userIdsWhoLikedFilm = likeService.getUserIdsWhoLikedFilm(film.getId());

        assertFalse(userIdsWhoLikedFilm.contains(user1.getId()));
    }

    @Test
    void shouldNotDuplicateLikes() {
        likeService.addLike(film.getId(), user2.getId());

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> likeService.addLike(film.getId(), user2.getId())
        );

        assertEquals("Данный пользователь уже добавлял лайк этому фильму!", exception.getMessage());
    }

    @Test
    void shouldThrowOnRemovingMissingLike() {
        assertThrows(NotFoundException.class, () -> likeService.deleteLike(1L, 9999L));
    }

    @Test
    void shouldReturnMostPopularFilmIds() {
        jdbc.update("DELETE FROM likes");

        Film secondFilm = filmService.create(new NewFilmRequest(
                "Second Film",
                "Another desc",
                LocalDate.of(2001, 1, 1),
                90,
                new MpaRating(1L, ""),
                List.of(new Genre(1L, ""))
        ));

        likeService.addLike(film.getId(), user1.getId());
        likeService.addLike(film.getId(), user2.getId());
        likeService.addLike(secondFilm.getId(), user1.getId());

        List<Long> topFilmIds = likeService.getTopFilmIds(2);

        System.out.println(film.getId());
        System.out.println(secondFilm.getId());

        System.out.println(topFilmIds);

        assertEquals(2, topFilmIds.size());
        assertEquals(film.getId(), topFilmIds.getFirst());
        assertEquals(secondFilm.getId(), topFilmIds.get(1));
    }
}