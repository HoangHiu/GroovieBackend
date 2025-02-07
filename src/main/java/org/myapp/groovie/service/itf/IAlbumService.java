package org.myapp.groovie.service.itf;

import org.myapp.groovie.dto.in.AlbumDtoIn;
import org.myapp.groovie.entity.album.Album;
import org.myapp.groovie.response.ApiCallException;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.UUID;

public interface IAlbumService {
    Album getOneAlbum(UUID albumId) throws ApiCallException;
    Page<Album> getAllAlbums(int pageNumber, int pageSize) throws ApiCallException;
    Album createAlbum(AlbumDtoIn albumDtoIn) throws ApiCallException;
    Album updateAlbum(UUID albumId, AlbumDtoIn albumDtoIn) throws ApiCallException;
    void deleteAlbum(UUID albumId) throws ApiCallException;
    List<Album> getAlbumsBasedOnIds(List<UUID> albumIds) throws ApiCallException;
    Page<Album> searchAlbum(String title, int pageNumber, int pageSize) throws ApiCallException;
}
