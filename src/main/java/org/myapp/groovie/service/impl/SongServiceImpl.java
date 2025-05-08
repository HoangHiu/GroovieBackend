package org.myapp.groovie.service.impl;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.myapp.groovie.dto.in.SongDtoIn;
import org.myapp.groovie.dto.out.AlbumDtoOut;
import org.myapp.groovie.dto.out.SongDtoOut;
import org.myapp.groovie.entity.album.Album;
import org.myapp.groovie.entity.song.Genre;
import org.myapp.groovie.entity.song.Song;
import org.myapp.groovie.repository.AlbumRepository;
import org.myapp.groovie.repository.GenreRepository;
import org.myapp.groovie.repository.SongRepository;
import org.myapp.groovie.response.ApiCallException;
import org.myapp.groovie.service.itf.IAlbumService;
import org.myapp.groovie.service.itf.IGenreService;
import org.myapp.groovie.service.itf.IS3Service;
import org.myapp.groovie.service.itf.ISongService;
import org.myapp.groovie.utility.specification.EntitySpecification;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.*;

@Service
@RequiredArgsConstructor
public class SongServiceImpl implements ISongService {
    private final SongRepository songRepository;
    private final GenreRepository genreRepository;
    private final AlbumRepository albumRepository;

    private final IGenreService genreService;
    private final IAlbumService albumService;

    private final IS3Service s3Service;

    @Value("${spring.data.aws.s3.album-bucket}")
    private String bucketName;

    @Value("${spring.data.aws.s3.route.album-cover-route}")
    private String coverRoute;

    @Override
    public Song getOneSong(UUID songId) throws ApiCallException {
        Optional<Song> song = songRepository.findById(songId);
        if(song.isPresent()){
            return song.get();
        }
        throw new ApiCallException("No song with id:" + songId, HttpStatus.NOT_FOUND);
    }

    @Override
    public Page<Song> getAllSongs(int pageNumber, int pageSize) throws ApiCallException {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<Song> songList = songRepository.findAll(pageable);
        if (!songList.isEmpty()){
            return songList;
        }
        throw new ApiCallException("No song found", HttpStatus.NOT_FOUND);
    }

    @Override
    public Song createSong(SongDtoIn songDtoIn) throws ApiCallException {
        Song songCreate = Song.fromSongDtoIn(songDtoIn);

        //init default values
        songCreate.setUuid(UUID.randomUUID());
        songCreate.setCreatedAt(new Timestamp(System.currentTimeMillis()));

        // relation values
        List<UUID> genreIds = songDtoIn.getGenreIds().stream().map(UUID::fromString).toList();
        UUID albumId = UUID.fromString(songDtoIn.getAlbumId());

        //get relations
        List<Genre> newGenre = genreService.getGenresBasedOnIds(genreIds);
        Album newAlbum = albumService.getOneAlbum(albumId);

        songCreate.setGenres(new HashSet<>(newGenre));
        songCreate.setAlbum(newAlbum);

        //add to relations
        songCreate.addToGenres((new HashSet<>(newGenre)));
        songCreate.addToAlbum(newAlbum);

        //save relations
//        genreRepository.saveAll(newGenre);
//        albumRepository.save(newAlbum);
        songRepository.save(songCreate);

        return songCreate;
    }

    @Override
    public Song updateSong(UUID songId, SongDtoIn songDtoIn) throws ApiCallException {
        Song songOrg = this.getOneSong(songId);

        //set initial values
        songOrg.getValuesFromDto(songDtoIn);
        songOrg.setUpdatedAt(new Timestamp(System.currentTimeMillis()));

        //get relation ids
        List<UUID> genreIds = songDtoIn.getGenreIds().stream().map(UUID::fromString).toList();
        UUID albumId = UUID.fromString(songDtoIn.getAlbumId());

        //get relations values
        List<Genre> newGenres = genreService.getGenresBasedOnIds(genreIds);
        Album newAlbum = albumService.getOneAlbum(albumId);

        //set new relation values
        songOrg.setAlbum(newAlbum);
        songOrg.setGenres(new HashSet<>(newGenres));

        //save values
        genreRepository.saveAll(newGenres);
        albumRepository.save(newAlbum);
        return songRepository.save(songOrg);
    }

    @Override
    public String deleteSong(UUID songId) throws ApiCallException {
        Song songDelete = this.getOneSong(songId);
        songDelete.removeAllGenres();
        songDelete.removeAlbum();
        songRepository.save(songDelete);
        songRepository.delete(songDelete);
        return "Deleted song with id: " + songId;
    }

    @Override
    public Page<SongDtoOut> searchSong(String title, int pageNumber, int pageSize) throws ApiCallException {
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
        Page<Song> songPage = songRepository.findAll(EntitySpecification.titleLike(title), pageable);

        if (!songPage.isEmpty()) {
            List<SongDtoOut> songDtoOutList = songPage.getContent()
                    .stream()
                    .map(song -> {
                        String albumCoverId = coverRoute + "/" + song.getAlbum().getUuid() + ".jpeg";
                        return SongDtoOut.fromSong(song, s3Service.getPresignedUrl(bucketName, albumCoverId));
                    })
                    .toList();

            return new PageImpl<>(songDtoOutList, pageable, songPage.getTotalElements());
        }

        throw new ApiCallException("No song found", HttpStatus.NOT_FOUND);
    }

    @Override
    public List<SongDtoOut> bulkGetSongsFromIds(List<String> songIds) {
        List<UUID> songUuids = songIds.stream().map(UUID::fromString).toList();
        List<Song> songList = songRepository.findAllByUuidIn(songUuids);

        List<SongDtoOut> res = new ArrayList<>();

        for (Song s : songList){
            String coverUrl = s3Service.getPresignedUrl(bucketName, coverRoute + "/" + s.getAlbum().getUuid() + ".jpeg");
            res.add(SongDtoOut.fromSongWithCover(s, coverUrl));
        }
        return res;
    }
}
