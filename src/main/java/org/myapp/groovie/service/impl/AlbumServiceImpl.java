package org.myapp.groovie.service.impl;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.myapp.groovie.dto.in.AlbumDtoIn;
import org.myapp.groovie.dto.out.AlbumDtoOut;
import org.myapp.groovie.entity.album.Album;
import org.myapp.groovie.entity.song.Song;
import org.myapp.groovie.entity.user.User;
import org.myapp.groovie.repository.AlbumRepository;
import org.myapp.groovie.repository.SongRepository;
import org.myapp.groovie.repository.UserRepository;
import org.myapp.groovie.response.ApiCallException;
import org.myapp.groovie.service.itf.IAlbumService;
import org.myapp.groovie.service.itf.IS3Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AlbumServiceImpl implements IAlbumService {

    private final AlbumRepository albumRepository;
    private final SongRepository songRepository;
    private final UserRepository userRepository;
    private final IS3Service s3Service;

    @Value("${spring.data.aws.s3.album-bucket}")
    private String bucketName;

    @Value("${spring.data.aws.s3.route.album-cover-route}")
    private String coverRoute;

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
    public Page<AlbumDtoOut> getAllAlbums(int pageNumber, int pageSize) throws ApiCallException{
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<Album> albumPage = albumRepository.findAll(pageable);
        if(!albumPage.isEmpty()){
            return new PageImpl<>(albumPage.getContent()
                    .stream()
                    .map(a -> {
                        String albumCoverId = coverRoute + "/" + a.getUuid() + ".jpeg";
                        return AlbumDtoOut.fromAlbum(a, s3Service.getPresignedUrl(bucketName, albumCoverId));
                    })
                    .toList());
        }
        throw new ApiCallException("No album found", HttpStatus.NOT_FOUND);
    }

    @Override
    public Album createAlbum(AlbumDtoIn albumDtoIn, Authentication authentication) {
        Album albumCreate = Album.fromDto(albumDtoIn);

        User user = userRepository.getUserByUsername(authentication.getName());

        //initial values
        albumCreate.setUuid(UUID.randomUUID());
        albumCreate.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        albumCreate.setUser(user);

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
        songRepository.deleteAllById(albumOrg.getSongs().stream().map(Song::getUuid).toList());
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

    @Override
    public List<Song> getSongsFromAlbumId(UUID albumId) throws ApiCallException {
        Album album = getOneAlbum(albumId);
        return album.getSongs().stream().toList();
    }
}
