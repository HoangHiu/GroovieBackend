package org.myapp.groovie.controller;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.myapp.groovie.dto.in.SongDtoIn;
import org.myapp.groovie.dto.out.SongDtoOut;
import org.myapp.groovie.entity.song.Song;
import org.myapp.groovie.response.ApiCallExecutor;
import org.myapp.groovie.response.ApiCallResponse;
import org.myapp.groovie.service.ApiExecutorService;
import org.myapp.groovie.service.itf.IS3Service;
import org.myapp.groovie.service.itf.ISongService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("v1/song")
public class SongController {
    private final ApiExecutorService<Object> apiExecutorService = new ApiExecutorService<>();
    private final ISongService songService;
    private final IS3Service s3Service;

    @Value("${spring.data.aws.s3.song-bucket}")
    private String bucketName;

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
            Song song = songService.getOneSong(UUID.fromString(songId));
            String url = s3Service.getPresignedUrl(bucketName, song.getUuid() + ".mp3");
           return new ApiCallResponse<>(SongDtoOut.fromSong(song, url));
        });
    }

    @PostMapping("")
    public ResponseEntity<ApiCallResponse<Object>> createSong(
            @RequestBody SongDtoIn songDtoIn){
        return apiExecutorService.execute(() -> {
            Song song = songService.createSong(songDtoIn);
            String url = s3Service.createPresignedUrl(bucketName, song.getUuid() + ".mp3");
            return new ApiCallResponse<>(SongDtoOut.fromSong(song, url));
        });
    }

    @PutMapping("/{uuid}")
    public ResponseEntity<ApiCallResponse<Object>> updateSong(
            @PathVariable("uuid") String songId,
            @RequestBody SongDtoIn songDtoIn
    ){
        return apiExecutorService.execute(() -> {
            Song song = songService.updateSong(UUID.fromString(songId), songDtoIn);
            String url = s3Service.createPresignedUrl(bucketName, song.getUuid() + ".mp3");
            return new ApiCallResponse<>(SongDtoOut.fromSong(song, url));
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
