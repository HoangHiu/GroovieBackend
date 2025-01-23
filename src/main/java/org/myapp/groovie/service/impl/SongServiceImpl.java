package org.myapp.groovie.service.impl;

import lombok.AllArgsConstructor;
import org.myapp.groovie.dto.in.SongDtoIn;
import org.myapp.groovie.entity.song.Song;
import org.myapp.groovie.repository.SongRepository;
import org.myapp.groovie.service.itf.ISongService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class SongServiceImpl implements ISongService {
    private final SongRepository songRepository;

    @Override
    public Song getOneSong(UUID songId) {
        return null;
    }

    @Override
    public List<Song> getAllSongs() {
        return songRepository.findAll();
    }

    @Override
    public Song createSong(SongDtoIn songDtoIn) {
        return null;
    }

    @Override
    public Song updateSong(UUID songId, SongDtoIn songDtoIn) {
        return null;
    }

    @Override
    public void deleteSong(UUID songId) {

    }
}
