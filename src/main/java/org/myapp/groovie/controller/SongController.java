package org.myapp.groovie.controller;

import lombok.AllArgsConstructor;
import org.myapp.groovie.dto.in.SongDtoIn;
import org.myapp.groovie.response.ApiCallExecutor;
import org.myapp.groovie.response.ApiCallResponse;
import org.myapp.groovie.service.ApiExecutorService;
import org.myapp.groovie.service.itf.ISongService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping("v1/song")
public class SongController {
    private final ApiExecutorService<Object> apiExecutorService = new ApiExecutorService<>();

    private final ISongService songService;

    @GetMapping("")
    public ResponseEntity<ApiCallResponse<Object>> getAllSongs(){
        return apiExecutorService.execute(() -> {
            return new ApiCallResponse<>(songService.getAllSongs());
        });
    }

    @GetMapping("/{uuid}")
    public ResponseEntity<ApiCallResponse<Object>> getOneSong(
            @PathVariable("uuid") String songId
    ){
        return apiExecutorService.execute(() -> {
           return new ApiCallResponse<>(songService.getOneSong(UUID.fromString(songId)));
        });
    }

    @PostMapping("")
    public ResponseEntity<ApiCallResponse<Object>> createSong(
            @RequestBody SongDtoIn songDtoIn){
        return apiExecutorService.execute(() -> {
            return new ApiCallResponse<>(songService.createSong(songDtoIn));
        });
    }

    @PutMapping("/{uuid}")
    public ResponseEntity<ApiCallResponse<Object>> updateSong(
            @PathVariable("uuid") String songId,
            @RequestBody SongDtoIn songDtoIn
    ){
        return apiExecutorService.execute(() -> {
            return new ApiCallResponse<>(songService.updateSong(UUID.fromString(songId), songDtoIn));
        });
    }

    @DeleteMapping("/{uuid}")
    public ResponseEntity<ApiCallResponse<Object>> deleteSong(
            @PathVariable("uuid") String songId
    ){
        return apiExecutorService.execute(() -> {
            return new ApiCallResponse<>(songService.deleteSong(UUID.fromString(songId)));
        });
    }
}
