package org.myapp.groovie.service.itf;

import org.myapp.groovie.dto.in.AlbumDtoIn;
import org.myapp.groovie.dto.out.AlbumDtoOut;
import org.myapp.groovie.entity.album.Album;
import org.myapp.groovie.entity.song.Song;
import org.myapp.groovie.response.ApiCallException;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;

import java.util.List;
import java.util.UUID;

public interface IAlbumService {
    Album getOneAlbum(UUID albumId) throws ApiCallException;
    Page<AlbumDtoOut> getAllAlbums(int pageNumber, int pageSize) throws ApiCallException;
    Album createAlbum(AlbumDtoIn albumDtoIn, Authentication authentication) throws ApiCallException;
    Album updateAlbum(UUID albumId, AlbumDtoIn albumDtoIn) throws ApiCallException;
    String deleteAlbum(UUID albumId) throws ApiCallException;
    List<Album> getAlbumsBasedOnIds(List<UUID> albumIds) throws ApiCallException;
    Page<Album> searchAlbum(String title, int pageNumber, int pageSize) throws ApiCallException;

    List<Song> getSongsFromAlbumId(UUID albumId) throws ApiCallException;
}
