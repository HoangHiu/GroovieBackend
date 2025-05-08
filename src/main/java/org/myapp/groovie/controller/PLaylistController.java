package org.myapp.groovie.controller;

import lombok.RequiredArgsConstructor;
import org.myapp.groovie.dto.in.PLaylistDtoIn;
import org.myapp.groovie.dto.out.PlaylistDtoOut;
import org.myapp.groovie.dto.request.AddSongsToPlaylistRequest;
import org.myapp.groovie.dto.request.DeleteSongFromPlaylistRequest;
import org.myapp.groovie.entity.playlist.Playlist;
import org.myapp.groovie.response.ApiCallResponse;
import org.myapp.groovie.service.ApiExecutorService;
import org.myapp.groovie.service.itf.IPlaylistService;
import org.myapp.groovie.service.itf.IS3Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/v1/playlist")
@RequiredArgsConstructor
public class PLaylistController {
    private final ApiExecutorService<Object> apiExecutorService = new ApiExecutorService<>();
    private final IPlaylistService playlistService;
    private final IS3Service s3Service;

    @Value("${spring.data.aws.s3.album-bucket}")
    private String bucketName;

    @Value("${spring.data.aws.s3.route.album-cover-route}")
    private String coverRoute;

    @GetMapping("/{id}")
    public ResponseEntity<ApiCallResponse<Object>> getPlaylistFromId(
            @PathVariable(name = "id") String playlistId
    ){
        return apiExecutorService.execute(() -> {

            Playlist playlist = playlistService.getPLaylistfromId(UUID.fromString(playlistId));
            Set<String> albumIds = playlist.getSongs().stream().map(s -> s.getAlbum().getUuid().toString()).collect(Collectors.toSet());
            List<String> albumCoverUrls = albumIds.stream().map(a ->  s3Service.getPresignedUrl(bucketName, coverRoute + "/" + a + ".jpeg")).toList();
            return new ApiCallResponse<>(PlaylistDtoOut.fromPlaylistWoSongs(playlist, albumCoverUrls));
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
