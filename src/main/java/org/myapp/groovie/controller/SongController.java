package org.myapp.groovie.controller;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.myapp.groovie.dto.in.SongDtoIn;
import org.myapp.groovie.dto.in.SongSearchDtoIn;
import org.myapp.groovie.dto.out.PageInfoDtoOut;
import org.myapp.groovie.dto.out.SongDtoOut;
import org.myapp.groovie.entity.song.Song;
import org.myapp.groovie.response.ApiCallExecutor;
import org.myapp.groovie.response.ApiCallResponse;
import org.myapp.groovie.service.ApiExecutorService;
import org.myapp.groovie.service.itf.IS3Service;
import org.myapp.groovie.service.itf.ISongService;
import org.myapp.groovie.service.itf.IUserService;
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
    private final IUserService userService;

    @Value("${spring.data.aws.s3.song-bucket}")
    private String bucketName;

    @Value("${spring.data.aws.s3.route.song-audio-route}")
    private String audioRoute;

    @GetMapping("")
    public ResponseEntity<ApiCallResponse<Object>> getAllSongs(
            @RequestParam(name = "page_number") int pageNumber,
            @RequestParam(name = "page_size") int pageSize
            ){
        return apiExecutorService.execute(() -> {
            return new ApiCallResponse<>(
                    PageInfoDtoOut.fromPage(songService.getAllSongs(pageNumber, pageSize)));
        });
    }

    @GetMapping("/search")
    public ResponseEntity<ApiCallResponse<Object>> searchSongs(
            @RequestParam(name = "title") String title,
            @RequestParam(name = "page_number") int pageNumber,
            @RequestParam(name = "page_size") int pageSize
    ){
        return apiExecutorService.execute(() -> {
            return new ApiCallResponse<>(
                    PageInfoDtoOut.fromPage(songService.searchSong(title, pageNumber, pageSize)));
        });
    }

    @GetMapping("/{uuid}")
    public ResponseEntity<ApiCallResponse<Object>> getOneSong(
            @PathVariable("uuid") String songId
    ){
        return apiExecutorService.execute(() -> {
            Song song = songService.getOneSong(UUID.fromString(songId));
           return new ApiCallResponse<>(SongDtoOut.fromSong(song, ""));
        });
    }

    @GetMapping("/{uuid}/audio")
    public ResponseEntity<ApiCallResponse<Object>> getSongFrom(
            @PathVariable("uuid") String songId
    ){
        return apiExecutorService.execute(() -> {
            String url = s3Service.getPresignedUrl(bucketName, audioRoute + "/" + songId + ".mp3");
            return new ApiCallResponse<>(url);
        });
    }

    @PostMapping("")
    public ResponseEntity<ApiCallResponse<Object>> createSong(
            @RequestBody SongDtoIn songDtoIn){
        return apiExecutorService.execute(() -> {
            Song song = songService.createSong(songDtoIn);
            String url = s3Service.createPresignedUrl(bucketName, audioRoute + "/" + song.getUuid() + ".mp3");
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
            String url = s3Service.createPresignedUrl(bucketName, audioRoute + "/" + song.getUuid() + ".mp3");
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
