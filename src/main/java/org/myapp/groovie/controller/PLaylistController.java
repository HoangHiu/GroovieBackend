package org.myapp.groovie.controller;

import lombok.RequiredArgsConstructor;
import org.myapp.groovie.dto.in.PLaylistDtoIn;
import org.myapp.groovie.dto.out.PlaylistDtoOut;
import org.myapp.groovie.dto.request.AddSongsToPlaylistRequest;
import org.myapp.groovie.dto.request.DeleteSongFromPlaylistRequest;
import org.myapp.groovie.response.ApiCallResponse;
import org.myapp.groovie.service.ApiExecutorService;
import org.myapp.groovie.service.itf.IPlaylistService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/v1/playlist")
@RequiredArgsConstructor
public class PLaylistController {
    private final ApiExecutorService<Object> apiExecutorService = new ApiExecutorService<>();
    private final IPlaylistService playlistService;

    @GetMapping("/{id}")
    public ResponseEntity<ApiCallResponse<Object>> getPlaylistFromId(
            @PathVariable(name = "id") String playlistId
    ){
        return apiExecutorService.execute(() -> {
            return new ApiCallResponse<>(PlaylistDtoOut.fromPlaylist(playlistService.getPLaylistfromId(UUID.fromString(playlistId))));
        });
    }

    @GetMapping("/me")
    public ResponseEntity<ApiCallResponse<Object>> getPersonalPlaylists(){
        return apiExecutorService.execute(() -> {
            return new ApiCallResponse<>(playlistService.getPersonalPlaylist());
        });
    }

    @PostMapping("/me")
    public ResponseEntity<ApiCallResponse<Object>> createPersonalPlaylists(
            @RequestBody PLaylistDtoIn pLaylistDtoIn
            ){
        return apiExecutorService.execute(() -> {
            return new ApiCallResponse<>(playlistService.createPersonalPlaylist(pLaylistDtoIn));
        });
    }

    @PutMapping("/me")
    public ResponseEntity<ApiCallResponse<Object>> updatePersonalPlaylists(
            @RequestParam(name = "playlistId") String playlistId,
            @RequestBody PLaylistDtoIn pLaylistDtoIn
    ){
        return apiExecutorService.execute(() -> {
            return new ApiCallResponse<>(playlistService.updatePersonalPlaylist(UUID.fromString(playlistId),pLaylistDtoIn));
        });
    }

    @DeleteMapping("/me")
    public ResponseEntity<ApiCallResponse<Object>> deletePersonalPlaylists(
            @RequestParam(name = "playlistId") String playlistId
    ){
        return apiExecutorService.execute(() -> {
            return new ApiCallResponse<>(playlistService.deletePersonalPlaylist(UUID.fromString(playlistId)));
        });
    }

    @GetMapping("/songs")
    public ResponseEntity<ApiCallResponse<Object>> getSongsInPlaylist(
            @RequestParam(name = "playlistId") String playlistId
    ){
        return apiExecutorService.execute(() -> {
            return new ApiCallResponse<>(playlistService.getSongsFromPlaylistId(UUID.fromString(playlistId)));
        });
    }

    @PostMapping("/songs")
    public ResponseEntity<ApiCallResponse<Object>> addSongsToPlaylist(
            @RequestBody AddSongsToPlaylistRequest addSongsToPlaylistRequest
            ){
        return apiExecutorService.execute(() -> {
            return new ApiCallResponse<>(playlistService.addSongsToPlaylist(
                    UUID.fromString(addSongsToPlaylistRequest.getPlaylistId()), addSongsToPlaylistRequest.getSongIds()));
        });
    }

    @DeleteMapping("/songs")
    public ResponseEntity<ApiCallResponse<Object>> deleteSongsFromPlaylist(
            @RequestBody DeleteSongFromPlaylistRequest deleteSongFromPlaylistRequest
    ){
        return apiExecutorService.execute(() -> {
            return new ApiCallResponse<>(playlistService.removeSongFromPlaylist(
                    UUID.fromString(deleteSongFromPlaylistRequest.getPlaylistId()), deleteSongFromPlaylistRequest.getSongId()));
        });
    }
}
