package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.filmdata.MpaRating;
import ru.yandex.practicum.filmorate.service.MpaRatingService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/mpa")
public class MpaController {
    private final MpaRatingService mpaRatingService;

    @GetMapping
    public List<MpaRating> getAllMpaRating() {
        return mpaRatingService.findAll();
    }

    @GetMapping("/{id}")
    public MpaRating getMpaRatingById(@PathVariable("id") Long mpaRatingId) {
        return mpaRatingService.getMpaRatingById(mpaRatingId);
    }
}