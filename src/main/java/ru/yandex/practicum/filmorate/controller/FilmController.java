package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.dto.NewFilmRequest;
import ru.yandex.practicum.filmorate.dto.UpdateFilmRequest;
import ru.yandex.practicum.filmorate.dto.UserDto;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.mapper.UserMapper;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.LikeService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/films")
public class FilmController {
    private final FilmService filmService;
    private final LikeService likeService;

    @GetMapping
    public List<FilmDto> findAll() {
        return filmService.findAll().stream()
                .map(FilmMapper::mapToFilmDto)
                .toList();
    }

    @PostMapping
    public FilmDto create(@Valid @RequestBody NewFilmRequest request) {
        return FilmMapper.mapToFilmDto(filmService.create(request));
    }

    @PutMapping
    public FilmDto update(@Valid @RequestBody UpdateFilmRequest request) {
        return FilmMapper.mapToFilmDto(filmService.update(request));
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable("id") Long filmId,
                        @PathVariable Long userId) {
        likeService.addLike(filmId, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLike(@PathVariable("id") Long filmId,
                           @PathVariable Long userId) {
        likeService.deleteLike(filmId, userId);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        filmService.delete(id);
    }

    @GetMapping("/{id}")
    public FilmDto getFilmById(@PathVariable("id") Long filmId) {
        return FilmMapper.mapToFilmDto(filmService.getFilmById(filmId));
    }

    @GetMapping("/{id}/likes/users")
    public List<UserDto> getUsersWhoLikedFilm(@PathVariable("id") Long filmId) {
        return filmService.getUsersWhoLikedFilm(filmId).stream()
                .map(UserMapper::mapToUserDto)
                .toList();
    }

    @GetMapping("/popular")
    public List<FilmDto> getTopFilms(@RequestParam(name = "count", defaultValue = "10")  int count) {
        return filmService.getTopFilms(count).stream()
                .map(FilmMapper::mapToFilmDto)
                .toList();
    }

    @GetMapping("/genres/{id}")
    public List<FilmDto> getFilmsByGenreId(@PathVariable("id") Long genreId) {
        return filmService.getFilmsByGenreId(genreId).stream()
                .map(FilmMapper::mapToFilmDto)
                .toList();
    }

    @GetMapping("/genres")
    public List<FilmDto> getFilmsByGenreIds(@RequestParam List<Long> genreIds) {
        return filmService.getFilmsByGenreIds(genreIds).stream()
                .map(FilmMapper::mapToFilmDto)
                .toList();
    }

    @GetMapping("/mpa/{id}")
    public List<FilmDto> getFilmsByMpaRatingId(@PathVariable("id") Long mpaRatingId) {
        return filmService.getFilmsByMpaRatingId(mpaRatingId).stream()
                .map(FilmMapper::mapToFilmDto)
                .toList();
    }
}