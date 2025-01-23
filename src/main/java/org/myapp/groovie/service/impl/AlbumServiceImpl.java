package org.myapp.groovie.service.impl;

import lombok.AllArgsConstructor;
import org.myapp.groovie.dto.in.AlbumDtoIn;
import org.myapp.groovie.entity.album.Album;
import org.myapp.groovie.repository.AlbumRepository;
import org.myapp.groovie.service.itf.IAlbumService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class AlbumServiceImpl implements IAlbumService {

    private final AlbumRepository albumRepository;

    @Override
    public Album getOneAlbum(UUID albumId) {
        return null;
    }

    @Override
    public List<Album> getAllAlbums() {
        return null;
    }

    @Override
    public Album createAlbum(AlbumDtoIn albumDtoIn) {
        return null;
    }

    @Override
    public Album updateAlbum(UUID albumId, AlbumDtoIn albumDtoIn) {
        return null;
    }

    @Override
    public void deleteAlbum(UUID albumId) {

    }
}
