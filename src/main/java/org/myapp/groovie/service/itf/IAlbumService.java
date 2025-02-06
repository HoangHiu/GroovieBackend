package org.myapp.groovie.service.itf;

import org.myapp.groovie.dto.in.AlbumDtoIn;
import org.myapp.groovie.entity.album.Album;
import org.myapp.groovie.response.ApiCallException;

import java.util.List;
import java.util.UUID;

public interface IAlbumService {
    Album getOneAlbum(UUID albumId) throws ApiCallException;
    List<Album> getAllAlbums();
    Album createAlbum(AlbumDtoIn albumDtoIn);
    Album updateAlbum(UUID albumId, AlbumDtoIn albumDtoIn);
    void deleteAlbum(UUID albumId);
    List<Album> getAlbumsBasedOnIds(List<UUID> albumIds) throws ApiCallException;
}
