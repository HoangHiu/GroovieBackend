package org.myapp.groovie.controller;

import lombok.RequiredArgsConstructor;
import org.myapp.groovie.dto.in.AlbumDtoIn;
import org.myapp.groovie.dto.out.AlbumDtoOut;
import org.myapp.groovie.dto.out.PageInfoDtoOut;
import org.myapp.groovie.entity.album.Album;
import org.myapp.groovie.response.ApiCallResponse;
import org.myapp.groovie.service.ApiExecutorService;
import org.myapp.groovie.service.itf.IAlbumService;
import org.myapp.groovie.service.itf.IS3Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

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

    @PostMapping("")
    public ResponseEntity<ApiCallResponse<Object>> createAlbum(
            @RequestBody AlbumDtoIn albumDtoIn
            ){
        return apiExecutorService.execute(() -> {
            Album album = albumService.createAlbum(albumDtoIn);
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
