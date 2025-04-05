package org.myapp.groovie.service.itf;

import org.myapp.groovie.dto.in.PLaylistDtoIn;
import org.myapp.groovie.dto.out.PlaylistDtoOut;
import org.myapp.groovie.dto.out.SongDtoOut;
import org.myapp.groovie.entity.playlist.Playlist;
import org.myapp.groovie.response.ApiCallException;

import java.util.List;
import java.util.UUID;

public interface IPlaylistService {
    List<Playlist> getPersonalPlaylist();
    Playlist getPLaylistfromId(UUID playlistId) throws ApiCallException;
    PlaylistDtoOut createPersonalPlaylist(PLaylistDtoIn pLaylistDtoIn) throws ApiCallException;
    PlaylistDtoOut updatePersonalPlaylist(UUID uuid, PLaylistDtoIn pLaylistDtoIn) throws ApiCallException;
    String deletePersonalPlaylist(UUID playlistId) throws ApiCallException;
    List<SongDtoOut> getSongsFromPlaylistId(UUID uuid) throws ApiCallException;
    PlaylistDtoOut addSongsToPlaylist(UUID playlistId, List<String> songIds) throws ApiCallException;
    String removeSongFromPlaylist(UUID playlistId, String songId) throws ApiCallException;

}
