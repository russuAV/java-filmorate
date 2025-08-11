package ru.yandex.practicum.filmorate.storage.db;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.filmdata.Genre;
import ru.yandex.practicum.filmorate.model.filmdata.MpaRating;
import ru.yandex.practicum.filmorate.storage.mapper.FilmRowMapper;
import ru.yandex.practicum.filmorate.storage.mapper.UserRowMapper;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@Import({LikeDbStorage.class, FilmDbStorage.class, FilmRowMapper.class, UserDbStorage.class, UserRowMapper.class})
class LikeDbStorageTest {

    @Autowired
    private LikeDbStorage likeDbStorage;

    @Autowired
    private FilmDbStorage filmDbStorage;

    @Autowired
    private UserDbStorage userDbStorage;

    @Autowired
    private JdbcTemplate jdbc;

    @Test
    void addAndRemoveLike() {
        jdbc.update("DELETE FROM likes");
        jdbc.update("DELETE FROM users");
        jdbc.update("DELETE FROM films");

        User user = userDbStorage.create(new User(null, "user@email.com", "login", "name",
                LocalDate.of(1990, 1, 1)));
        Film film = filmDbStorage.create(new Film(null, LocalDate.of(2000, 1, 1),
                "Film Title", "Film Desc", 120,
                List.of(new Genre(1L, "Comedy")),
                new MpaRating(1L, "G")));

        likeDbStorage.addLike(film.getId(), user.getId());
        List<Long> likedUsers = likeDbStorage.getUserIdsWhoLikedFilm(film.getId());

        assertThat(likedUsers).hasSize(1).contains(user.getId());

        likeDbStorage.removeLike(film.getId(), user.getId());
        likedUsers = likeDbStorage.getUserIdsWhoLikedFilm(film.getId());

        assertThat(likedUsers).isEmpty();
    }

    @Test
    void getLikesForFilmWithMultipleUsers() {
        jdbc.update("DELETE FROM likes");
        jdbc.update("DELETE FROM users");
        jdbc.update("DELETE FROM films");

        Film film = filmDbStorage.create(new Film(null, LocalDate.of(2010, 10, 10),
                "Liked Film", "Description", 100,
                List.of(new Genre(2L, "Drama")), new MpaRating(2L, "PG")));

        User user1 = userDbStorage.create(new User(null, "one@email.com", "user1", "User One",
                LocalDate.of(1985, 5, 5)));
        User user2 = userDbStorage.create(new User(null, "two@email.com", "user2", "User Two",
                LocalDate.of(1992, 8, 8)));

        likeDbStorage.addLike(film.getId(), user1.getId());
        likeDbStorage.addLike(film.getId(), user2.getId());

        List<Long> likes = likeDbStorage.getUserIdsWhoLikedFilm(film.getId());

        assertThat(likes).hasSize(2).containsExactlyInAnyOrder(user1.getId(), user2.getId());
    }

    @Test
    void getTopFilmsShouldReturnSortedByLikes() {
        jdbc.update("DELETE FROM likes");
        jdbc.update("DELETE FROM users");
        jdbc.update("DELETE FROM films");

        User user1 = userDbStorage.create(new User(null, "user1@mail.com", "user1", "User One",
                LocalDate.of(1990, 1, 1)));
        User user2 = userDbStorage.create(new User(null, "user2@mail.com", "user2", "User Two",
                LocalDate.of(1991, 2, 2)));
        User user3 = userDbStorage.create(new User(null, "user3@mail.com", "user3", "User Three",
                LocalDate.of(1992, 3, 3)));

        Film film1 = filmDbStorage.create(new Film(null, LocalDate.of(2000, 1, 1),
                "Film 1", "Desc 1", 100,
                List.of(new Genre(1L, "Comedy")), new MpaRating(1L, "G")));

        Film film2 = filmDbStorage.create(new Film(null, LocalDate.of(2001, 2, 2),
                "Film 2", "Desc 2", 120,
                List.of(new Genre(2L, "Drama")), new MpaRating(2L, "PG")));

        Film film3 = filmDbStorage.create(new Film(null, LocalDate.of(2002, 3, 3),
                "Film 3", "Desc 3", 90,
                List.of(new Genre(3L, "Action")), new MpaRating(3L, "PG-13")));

        likeDbStorage.addLike(film2.getId(), user1.getId());
        likeDbStorage.addLike(film2.getId(), user2.getId());
        likeDbStorage.addLike(film3.getId(), user1.getId());

        List<Long> topFilmIds = likeDbStorage.getTopFilmIds(3);

        assertThat(topFilmIds).hasSize(2);
        assertThat(topFilmIds.get(0)).isEqualTo(film2.getId());
        assertThat(topFilmIds.get(1)).isEqualTo(film3.getId());
    }
}