package org.myapp.groovie.service.impl;

import lombok.AllArgsConstructor;
import org.myapp.groovie.dto.in.AlbumDtoIn;
import org.myapp.groovie.entity.album.Album;
import org.myapp.groovie.entity.song.Song;
import org.myapp.groovie.repository.AlbumRepository;
import org.myapp.groovie.repository.SongRepository;
import org.myapp.groovie.response.ApiCallException;
import org.myapp.groovie.service.itf.IAlbumService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class AlbumServiceImpl implements IAlbumService {

    private final AlbumRepository albumRepository;
    private final SongRepository songRepository;

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
    public Page<Album> getAllAlbums(int pageNumber, int pageSize) throws ApiCallException{
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<Album> albumPage = albumRepository.findAll(pageable);
        if(!albumPage.isEmpty()){
            return albumPage;
        }
        throw new ApiCallException("No album found", HttpStatus.NOT_FOUND);
    }

    @Override
    public Album createAlbum(AlbumDtoIn albumDtoIn) {
        Album albumCreate = Album.fromDto(albumDtoIn);

        //initial values
        albumCreate.setUuid(UUID.randomUUID());
        albumCreate.setCreatedAt(new Timestamp(System.currentTimeMillis()));

        return albumRepository.save(albumCreate);
    }

    @Override
    public Album updateAlbum(UUID albumId, AlbumDtoIn albumDtoIn) throws ApiCallException {
        Album albumOrg = getOneAlbum(albumId);
        Album albumUpdate = Album.fromDto(albumDtoIn);

        //set init values
        albumUpdate.setUuid(albumId);
        albumUpdate.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        albumUpdate.setSongs(albumOrg.getSongs());
        albumUpdate.setCreatedAt(albumOrg.getCreatedAt());

        //set relational values
        songRepository.saveAll(albumUpdate.getSongs());

        return albumRepository.save(albumUpdate);
    }

    @Override
    public String deleteAlbum(UUID albumId) throws ApiCallException {
        Album albumOrg = getOneAlbum(albumId);
        albumRepository.delete(albumOrg);
        return "Deleted album with id: " + albumId;
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

    @Override
    public Page<Album> searchAlbum(String title, int pageNumber, int pageSize) throws ApiCallException {
        return null;
    }
}
