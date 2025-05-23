package org.myapp.groovie.service.impl;

import lombok.RequiredArgsConstructor;
import org.myapp.groovie.dto.in.AlbumDtoIn;
import org.myapp.groovie.dto.out.AlbumDtoOut;
import org.myapp.groovie.dto.request.DeleteSongFromAlbumRequest;
import org.myapp.groovie.entity.album.Album;
import org.myapp.groovie.entity.playlist.Playlist;
import org.myapp.groovie.entity.song.Song;
import org.myapp.groovie.entity.user.User;
import org.myapp.groovie.repository.AlbumRepository;
import org.myapp.groovie.repository.PlaylistRepository;
import org.myapp.groovie.repository.SongRepository;
import org.myapp.groovie.repository.UserRepository;
import org.myapp.groovie.response.ApiCallException;
import org.myapp.groovie.service.itf.IAlbumService;
import org.myapp.groovie.service.itf.IPlaylistService;
import org.myapp.groovie.service.itf.IS3Service;
import org.myapp.groovie.service.itf.ISongService;
import org.myapp.groovie.utility.specification.EntitySpecification;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AlbumServiceImpl implements IAlbumService {

    private final AlbumRepository albumRepository;
    private final SongRepository songRepository;
    private final UserRepository userRepository;
    private final PlaylistRepository playlistRepository;
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
    public Page<AlbumDtoOut> getAllAlbums(String title, String userId, int pageNumber, int pageSize) throws ApiCallException {
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
        Page<Album> albumPage = albumRepository.findAll(EntitySpecification.columnLike(title, userId), pageable);

        if (!albumPage.isEmpty()) {
            List<AlbumDtoOut> albumDtoOutList = albumPage.getContent()
                    .stream()
                    .map(a -> {
                        String albumCoverId = coverRoute + "/" + a.getUuid() + ".jpeg";
                        return AlbumDtoOut.fromAlbum(a, s3Service.getPresignedUrl(bucketName, albumCoverId));
                    })
                    .toList();

            return new PageImpl<>(albumDtoOutList, pageable, albumPage.getTotalElements());
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
        //set init values
        albumOrg.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        albumOrg.setTitle(albumDtoIn.getTitle());
        albumOrg.setReleaseYear(albumDtoIn.getReleaseYear());

        //set relational values
//        songRepository.saveAll(albumOrg.getSongs());

        return albumRepository.save(albumOrg);
    }

    @Override
    public String deleteAlbum(UUID albumId) throws ApiCallException {
        Album albumOrg = getOneAlbum(albumId);
        albumRepository.deleteAllSongsAndAlbum(albumOrg.getUuid());
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
        return album.getSongs().stream()
                .sorted(Comparator.comparing(Song::getCreatedAt))
                .toList();
    }

    @Override
    public Page<AlbumDtoOut> getAlbumsFromUsername(String username, int pageNumber, int pageSize) throws ApiCallException {
        User user = userRepository.getUserByUsername(username);
        if(user == null){
            throw new ApiCallException("User with username: " + username + " not found", HttpStatus.NOT_FOUND);
        }

        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<Album> albumPage = albumRepository.findAllByUser(user, pageable);
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
    public String deleteSongFromAlbum(DeleteSongFromAlbumRequest request) throws ApiCallException {
        Album album = getOneAlbum(UUID.fromString(request.getAlbumId()));
        Optional<Song> fetchedSong = songRepository.findById(UUID.fromString(request.getSongId()));

        Set<Song> orgSongs = album.getSongs();


        if(fetchedSong.isPresent()){
            songRepository.deleteSongAndRelations(fetchedSong.get().getUuid());
            orgSongs.remove(fetchedSong.get());
            album.setSongs(orgSongs);
            albumRepository.save(album);
            return "Removed song with id: " + request.getSongId() + " from album with id: " + request.getAlbumId();
        }else
            throw new ApiCallException("Song with id: " + request.getSongId() + " not found", HttpStatus.NOT_FOUND);
    }

}
