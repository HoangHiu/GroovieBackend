package org.myapp.groovie.controller;

import lombok.AllArgsConstructor;
import org.myapp.groovie.dto.in.SongDtoIn;
import org.myapp.groovie.response.ApiCallResponse;
import org.myapp.groovie.service.ApiExecutorService;
import org.myapp.groovie.service.itf.ISongService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("v1/song")
public class SongController {
    private final ApiExecutorService<Object> apiExecutorService = new ApiExecutorService<>();

    private final ISongService songService;

    @PostMapping("")
    public ResponseEntity<ApiCallResponse<Object>> createSong(
            @RequestBody SongDtoIn songDtoIn){
        return apiExecutorService.execute(() -> {
            return new ApiCallResponse<>(songService.createSong(songDtoIn));
        });
    }
}
