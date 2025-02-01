package org.myapp.groovie.service.itf;

import org.myapp.groovie.dto.in.GenreDtoIn;
import org.myapp.groovie.entity.song.Genre;
import org.myapp.groovie.response.ApiCallException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface IGenreService {
    Genre getOneGenre(UUID genreId);

    List<Genre> getAllGenres();

    Genre createGenre(GenreDtoIn genreDtoIn);

    Genre updateGenre(UUID genreId, GenreDtoIn genreDtoIn);

    void deleteGenre(UUID genreId);

    List<Genre> getGenresBasedOnIds(List<UUID> genreIds) throws ApiCallException;
}
