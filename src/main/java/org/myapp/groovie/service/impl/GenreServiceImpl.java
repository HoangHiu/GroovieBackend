package org.myapp.groovie.service.impl;

import lombok.AllArgsConstructor;
import org.myapp.groovie.dto.in.GenreDtoIn;
import org.myapp.groovie.entity.song.Genre;
import org.myapp.groovie.repository.GenreRepository;
import org.myapp.groovie.response.ApiCallException;
import org.myapp.groovie.service.itf.IGenreService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.*;

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
        return genreRepository.findAll();
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

    @Override
    public List<Genre> getGenresBasedOnIds(List<UUID> genreIds) throws ApiCallException {
        List<Genre> genreSet = new ArrayList<>();
        for(UUID genreId : genreIds){
            Optional<Genre> genreOpt = genreRepository.findById(genreId);
            if(genreOpt.isPresent()){
                genreSet.add(genreOpt.get());
            }else{
                throw new ApiCallException("Genre with id: " + genreId + "does not exists", HttpStatus.NOT_FOUND);
            }
        }
        return genreSet;
    }
}
