package org.myapp.groovie.repository;

import org.myapp.groovie.entity.song.Genre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Repository
public interface GenreRepository extends JpaRepository<Genre, UUID> {
    List<Genre> findByUuidIn(Set<UUID> uuids);
}
