package org.myapp.groovie.service.impl;

import lombok.RequiredArgsConstructor;
import org.myapp.groovie.dto.in.PLaylistDtoIn;
import org.myapp.groovie.dto.out.PlaylistDtoOut;
import org.myapp.groovie.dto.out.SongDtoOut;
import org.myapp.groovie.entity.playlist.Playlist;
import org.myapp.groovie.entity.song.Song;
import org.myapp.groovie.entity.user.User;
import org.myapp.groovie.repository.PlaylistRepository;
import org.myapp.groovie.repository.SongRepository;
import org.myapp.groovie.response.ApiCallException;
import org.myapp.groovie.service.itf.IPlaylistService;
import org.myapp.groovie.service.itf.IS3Service;
import org.myapp.groovie.service.itf.ISongService;
import org.myapp.groovie.service.itf.IUserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PlaylistServiceImpl implements IPlaylistService {
    private final PlaylistRepository playlistRepository;
    private final SongRepository songRepository;
    private final ISongService songService;
    private final IUserService userService;
    private final IS3Service s3Service;

    @Value("${spring.data.aws.s3.album-bucket}")
    private String bucketName;

    @Value("${spring.data.aws.s3.route.album-cover-route}")
    private String coverRoute;

    @Override
    public List<PlaylistDtoOut> getPersonalPlaylist() {
        List<PlaylistDtoOut> res = new ArrayList<>();

        List<Playlist> playlists = playlistRepository.findAllByUsername(SecurityContextHolder.getContext().getAuthentication().getName());
        for(Playlist p : playlists){
            Set<String> albumIds = p.getSongs().stream().map(s -> s.getAlbum().getUuid().toString()).collect(Collectors.toSet());
            List<String> albumCoverUrls = albumIds.stream().map(a ->  s3Service.getPresignedUrl(bucketName, coverRoute + "/" + a + ".jpeg")).toList();
            res.add(PlaylistDtoOut.fromPlaylistWoSongs(p, albumCoverUrls));
        }

        return res;
    }

    @Override
    public Playlist getPLaylistfromId(UUID playlistId) throws ApiCallException {
        Optional<Playlist> playlistOptional = playlistRepository.findById(playlistId);
        if (!playlistOptional.isPresent()){
            throw new ApiCallException("Playlist with id: " + playlistId.toString() + " not found", HttpStatus.NOT_FOUND);
        }
        return playlistOptional.get();
    }

    @Override
    public PlaylistDtoOut createPersonalPlaylist(PLaylistDtoIn pLaylistDtoIn) throws ApiCallException {
        Playlist playlist = Playlist.fromDtoIn(pLaylistDtoIn);
        playlist.setUuid(UUID.randomUUID());
        playlist.setCreatedAt(new Timestamp(System.currentTimeMillis()));

        User user = userService.getOneByUsername(SecurityContextHolder.getContext().getAuthentication().getName());
        playlist.setUser(user);
        return PlaylistDtoOut.fromPlaylist(playlistRepository.save(playlist));
    }

    @Override
    public PlaylistDtoOut updatePersonalPlaylist(UUID uuid, PLaylistDtoIn pLaylistDtoIn) throws ApiCallException {
        Playlist playlist = this.getPLaylistfromId(uuid);
        playlist.setName(pLaylistDtoIn.getName());
        playlist.setDescription(pLaylistDtoIn.getDescription());
        playlist.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        return PlaylistDtoOut.fromPlaylist(playlistRepository.save(playlist));
    }

    @Override
    public String deletePersonalPlaylist(UUID playlistId) throws ApiCallException {
        Playlist playlist = getPLaylistfromId(playlistId);
        playlistRepository.delete(playlist);
        return "Deleted playlist with id: " + playlist.getUuid().toString();
    }

    @Override
    public List<SongDtoOut> getSongsFromPlaylistId(UUID uuid) throws ApiCallException {
        Playlist playlist = getPLaylistfromId(uuid);
        return playlist.getSongs().stream().map(s ->
                SongDtoOut.fromSong(s, s3Service.getPresignedUrl(bucketName, coverRoute + "/" + s.getAlbum().getUuid() + ".jpeg")))
                .toList();
    }

    @Override
    @Transactional
    public PlaylistDtoOut addSongsToPlaylist(UUID playlistId, List<String> songIds) throws ApiCallException {
        Playlist playlist = getPLaylistfromId(playlistId);
        List<Song> fetchedSongs = new ArrayList<>();
        for (String songId : songIds){
            fetchedSongs.add(songService.getOneSong(UUID.fromString(songId)));
        }

        List<Song> orgSongs = playlist.getSongs();
        orgSongs.addAll(fetchedSongs);
        playlist.setSongs(orgSongs);

        for (Song s : orgSongs){
            s.getPlaylists().add(playlist);
        }

        songRepository.saveAll(orgSongs);

        return PlaylistDtoOut.fromPlaylist(playlistRepository.save(playlist));
    }

    @Override
    public String removeSongFromPlaylist(UUID playlistId, String songId) throws ApiCallException {
        Playlist playlist = getPLaylistfromId(playlistId);
        Song fetchedSong = songService.getOneSong(UUID.fromString(songId));

        List<Song> orgSongs = playlist.getSongs();
        orgSongs.remove(fetchedSong);

        for (Song s : orgSongs){
            s.getPlaylists().remove(playlist);
        }

        playlist.setSongs(orgSongs);
        songRepository.saveAll(orgSongs);
        playlistRepository.save(playlist);
        return "Removed songs with ids: " + songId + " from playlist with id: " + playlistId;
    }
}
