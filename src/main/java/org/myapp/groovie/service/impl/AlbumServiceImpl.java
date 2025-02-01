package org.myapp.groovie.service.impl;

import lombok.AllArgsConstructor;
import org.myapp.groovie.dto.in.AlbumDtoIn;
import org.myapp.groovie.entity.album.Album;
import org.myapp.groovie.repository.AlbumRepository;
import org.myapp.groovie.response.ApiCallException;
import org.myapp.groovie.service.itf.IAlbumService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class AlbumServiceImpl implements IAlbumService {

    private final AlbumRepository albumRepository;

    @Override
    public Album getOneAlbum(UUID albumId) throws ApiCallException {
        Optional<Album> albumOpt = albumRepository.findById(albumId);
        if(albumOpt.isPresent()){
            return albumOpt.get();
        }else{
            throw new ApiCallException("Album with id: " + albumId + "does not exists", HttpStatus.NOT_FOUND);
        }
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

    @Override
    public List<Album> getAlbumsBasedOnIds(List<UUID> albumIds) throws ApiCallException {
        List<Album> albumList = new ArrayList<>();
        for(UUID albumId : albumIds){
            Optional<Album> albumOpt = albumRepository.findById(albumId);
            if(albumOpt.isPresent()){
                albumList.add(albumOpt.get());
            }else{
                throw new ApiCallException("Album with id: " + albumId + "does not exists", HttpStatus.NOT_FOUND);
            }
        }
        return albumList;
    }
}
