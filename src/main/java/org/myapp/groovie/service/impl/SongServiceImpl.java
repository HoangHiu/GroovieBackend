package org.myapp.groovie.service.impl;

import lombok.AllArgsConstructor;
import org.myapp.groovie.dto.in.SongDtoIn;
import org.myapp.groovie.entity.album.Album;
import org.myapp.groovie.entity.song.Genre;
import org.myapp.groovie.entity.song.Song;
import org.myapp.groovie.repository.AlbumRepository;
import org.myapp.groovie.repository.GenreRepository;
import org.myapp.groovie.repository.SongRepository;
import org.myapp.groovie.response.ApiCallException;
import org.myapp.groovie.service.itf.IAlbumService;
import org.myapp.groovie.service.itf.IGenreService;
import org.myapp.groovie.service.itf.ISongService;
import org.myapp.groovie.utility.specification.SongSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.*;

@Service
@AllArgsConstructor
public class SongServiceImpl implements ISongService {
    private final SongRepository songRepository;
    private final GenreRepository genreRepository;
    private final AlbumRepository albumRepository;

    private final IGenreService genreService;
    private final IAlbumService albumService;

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
    public Page<Song> searchSongs(String keywords, int pageNumber, int pageSize) throws ApiCallException {
        return null;
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

        //add to relations
        songCreate.addToGenres((new HashSet<>(newGenre)));
        songCreate.addToAlbum(newAlbum);

        //save relations
        genreRepository.saveAll(newGenre);
        albumRepository.save(newAlbum);
        songRepository.save(songCreate);

        return songCreate;
    }

    @Override
    public Song updateSong(UUID songId, SongDtoIn songDtoIn) throws ApiCallException {
        Song songUpdate = Song.fromSongDtoIn(songDtoIn);
        Song songOrg = this.getOneSong(songId);

        //set initial values
        songUpdate.setCreatedAt(songOrg.getCreatedAt());
        songUpdate.setUpdatedAt(new Timestamp(System.currentTimeMillis()));

        //get relation ids
        List<UUID> genreIds = songDtoIn.getGenreIds().stream().map(UUID::fromString).toList();
        UUID albumId = UUID.fromString(songDtoIn.getAlbumId());

        //get relations values
        List<Genre> newGenres = genreService.getGenresBasedOnIds(genreIds);
        Album newAlbum = albumService.getOneAlbum(albumId);

        //remove original relation values
        songUpdate.removeFromAlbum(songOrg.getAlbum());
        songUpdate.removeFromGenres(songOrg.getGenres());

        //set new relation values
        songUpdate.addToAlbum(newAlbum);
        songUpdate.addToGenres(new HashSet<>(newGenres));

        //save values
/*        genreRepository.saveAll(newGenres);
        albumRepository.save(newAlbum);*/

        songUpdate.setUuid(songId);

        return songRepository.save(songUpdate);
    }

    @Override
    public String deleteSong(UUID songId) throws ApiCallException {
        Song songDelete = this.getOneSong(songId);
        songRepository.delete(songDelete);
        return "Deleted song with id: " + songId;
    }

    @Override
    public Page<Song> searchSong(String title, int pageNumber, int pageSize) throws ApiCallException {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<Song> songPage = songRepository.findAll(SongSpecification.titleLike(title), pageable);
        if (!songPage.isEmpty()){
            return songPage;
        }
        throw new ApiCallException("No song found", HttpStatus.NOT_FOUND);
    }
}
