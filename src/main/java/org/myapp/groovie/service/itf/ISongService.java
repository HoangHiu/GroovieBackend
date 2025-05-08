package org.myapp.groovie.service.itf;

import org.myapp.groovie.dto.in.SongDtoIn;
import org.myapp.groovie.dto.out.SongDtoOut;
import org.myapp.groovie.entity.song.Song;
import org.myapp.groovie.response.ApiCallException;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.UUID;

public interface ISongService {
    Song getOneSong(UUID songId) throws ApiCallException;
    Page<Song> getAllSongs(int pageNumber, int pageSize) throws ApiCallException;
    Song createSong(SongDtoIn songDtoIn) throws ApiCallException;
    Song updateSong(UUID songId, SongDtoIn songDtoIn) throws ApiCallException;
    String deleteSong(UUID songId) throws ApiCallException;
    Page<SongDtoOut> searchSong(String title, int pageNumber, int pageSize) throws ApiCallException;
    List<SongDtoOut> bulkGetSongsFromIds(List<String> songIds);
}
