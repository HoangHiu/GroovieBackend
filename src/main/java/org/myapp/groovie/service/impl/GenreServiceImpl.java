package org.myapp.groovie.service.impl;

import lombok.AllArgsConstructor;
import org.myapp.groovie.dto.in.GenreDtoIn;
import org.myapp.groovie.entity.song.Genre;
import org.myapp.groovie.repository.GenreRepository;
import org.myapp.groovie.service.itf.IGenreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class GenreServiceImpl implements IGenreService {

    private final GenreRepository genreRepository;

    @Override
    public Genre getOneGenre(UUID genreId) {
        return null;
    }

    @Override
    public List<Genre> getAllGenres() {
        return null;
    }

    @Override
    public Genre createGenre(GenreDtoIn genreDtoIn) {
        return null;
    }

    @Override
    public Genre updateGenre(UUID genreId, GenreDtoIn genreDtoIn) {
        return null;
    }

    @Override
    public void deleteGenre(UUID genreId) {

    }
}
