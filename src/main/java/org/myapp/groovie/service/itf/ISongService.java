package org.myapp.groovie.service.itf;

import org.myapp.groovie.dto.in.SongDtoIn;
import org.myapp.groovie.entity.song.Song;
import org.myapp.groovie.response.ApiCallException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

public interface ISongService {
    Song getOneSong(UUID songId) throws ApiCallException;
    List<Song> getAllSongs() throws ApiCallException;
    Song createSong(SongDtoIn songDtoIn) throws ApiCallException;
    Song updateSong(UUID songId, SongDtoIn songDtoIn);
    void deleteSong(UUID songId);
}
