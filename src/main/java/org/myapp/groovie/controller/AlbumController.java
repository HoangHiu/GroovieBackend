package org.myapp.groovie.controller;

import lombok.RequiredArgsConstructor;
import org.myapp.groovie.dto.in.AlbumDtoIn;
import org.myapp.groovie.dto.in.S3ObjectDtoIn;
import org.myapp.groovie.dto.out.AlbumDtoOut;
import org.myapp.groovie.dto.out.PageInfoDtoOut;
import org.myapp.groovie.dto.request.AddSongsToAlbumRequest;
import org.myapp.groovie.dto.request.DeleteSongFromAlbumRequest;
import org.myapp.groovie.entity.album.Album;
import org.myapp.groovie.response.ApiCallResponse;
import org.myapp.groovie.service.ApiExecutorService;
import org.myapp.groovie.service.itf.IAlbumService;
import org.myapp.groovie.service.itf.IS3Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("v1/album")
@RequiredArgsConstructor
public class AlbumController {
    private final IAlbumService albumService;
    private final IS3Service s3Service;
    private final ApiExecutorService<Object> apiExecutorService = new ApiExecutorService<>();

    @Value("${spring.data.aws.s3.album-bucket}")
    private String bucketName;

    @Value("${spring.data.aws.s3.route.album-cover-route}")
    private String coverRoute;

    @GetMapping("/{uuid}")
    public ResponseEntity<ApiCallResponse<Object>> getOneAlbum(
            @PathVariable("uuid") String albumId
    ){
        return apiExecutorService.execute(() -> {
            Album album = albumService.getOneAlbum(UUID.fromString(albumId));
            String url = s3Service.createPresignedUrl(bucketName, coverRoute + "/" + album.getUuid() + ".jpeg");
            return new ApiCallResponse<>(AlbumDtoOut.fromAlbum(album, url));
        });
    }

    @GetMapping("")
    public ResponseEntity<ApiCallResponse<Object>> getAllAlbums(
            @RequestParam(name = "page_number") int pageNumber,
            @RequestParam(name = "page_size") int pageSize
    ){
        return apiExecutorService.execute(() -> {
            return new ApiCallResponse<>(PageInfoDtoOut.fromPage(albumService.getAllAlbums(pageNumber, pageSize)));
        });
    }

    @GetMapping("/{uuid}/getSongs")
    public ResponseEntity<ApiCallResponse<Object>> getSongsFromAlbum(
            @PathVariable(name = "uuid") String albumId
    ){
        return apiExecutorService.execute(() -> {
            return new ApiCallResponse<>(albumService.getSongsFromAlbumId(UUID.fromString(albumId)));
        });
    }

    @GetMapping("/me")
    public ResponseEntity<ApiCallResponse<Object>> getPersonalAlbums(
            @RequestParam(name = "page_number") int pageNumber,
            @RequestParam(name = "page_size") int pageSize,
            Authentication authentication
    ){
        return apiExecutorService.execute(() -> {
            String username = authentication.getName();
            return new ApiCallResponse<>(PageInfoDtoOut.fromPage(albumService.getAlbumsFromUsername(username, pageNumber, pageSize)));
        });
    }

    @PostMapping("/get-bulk-cover")
    public ResponseEntity<ApiCallResponse<Object>> getBulkAlbumCover(
            @RequestBody List<String> objectNames
    ){
        return apiExecutorService.execute(() -> {
            Set<String> objectNamesAlt = objectNames.stream().map(so ->
                coverRoute + "/" + so + ".jpeg")
                    .collect(Collectors.toSet());
            return new ApiCallResponse<>(s3Service.getBulkPresignedUrl(bucketName, objectNamesAlt));
        });
    }

    @PostMapping("")
    public ResponseEntity<ApiCallResponse<Object>> createAlbum(
            @RequestBody AlbumDtoIn albumDtoIn,
            Authentication authentication
            ){
        return apiExecutorService.execute(() -> {
            Album album = albumService.createAlbum(albumDtoIn, authentication);
            String url = s3Service.createPresignedUrl(bucketName, coverRoute + "/" + album.getUuid() + ".jpeg");
            return new ApiCallResponse<>(AlbumDtoOut.fromAlbum(album, url));
        });
    }

    @PutMapping("/{uuid}")
    public ResponseEntity<ApiCallResponse<Object>> updateAlbum(
            @PathVariable("uuid") String albumId,
            @RequestBody AlbumDtoIn albumDtoIn
    ){
        return apiExecutorService.execute(() -> {
            Album album = albumService.updateAlbum(UUID.fromString(albumId), albumDtoIn);
            String url = s3Service.createPresignedUrl(bucketName, coverRoute + "/" + album.getUuid() + ".jpeg");
            return new ApiCallResponse<>(AlbumDtoOut.fromAlbum(album, url));
        });
    }

    @DeleteMapping("/{uuid}")
    public ResponseEntity<ApiCallResponse<Object>> deleteAlbum(
            @PathVariable("uuid") String albumId
    ){
        return apiExecutorService.execute(() -> {
            return new ApiCallResponse<>(albumService.deleteAlbum(UUID.fromString(albumId)));
        });
    }
}
