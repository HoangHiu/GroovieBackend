package org.myapp.groovie.service.itf;

import org.myapp.groovie.dto.in.SongDtoIn;
import org.myapp.groovie.entity.song.Song;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

public interface ISongService {
    Song getOneSong(UUID songId);
    List<Song> getAllSongs();
    Song createSong(SongDtoIn songDtoIn);
    Song updateSong(UUID songId, SongDtoIn songDtoIn);
    void deleteSong(UUID songId);
}
