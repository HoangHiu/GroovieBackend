package org.myapp.groovie.controller;

import lombok.RequiredArgsConstructor;
import org.myapp.groovie.dto.out.AlbumDtoOut;
import org.myapp.groovie.entity.album.Album;
import org.myapp.groovie.response.ApiCallResponse;
import org.myapp.groovie.service.ApiExecutorService;
import org.myapp.groovie.service.itf.IGenreService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("v1/genre")
@RequiredArgsConstructor
public class GenreController {
    private final IGenreService genreService;

    private final ApiExecutorService<Object> apiExecutorService = new ApiExecutorService<>();

    @GetMapping("")
    public ResponseEntity<ApiCallResponse<Object>> getAllGenres(
    ){
        return apiExecutorService.execute(() -> {
            return new ApiCallResponse<>(genreService.getAllGenres());
        });
    }

}
