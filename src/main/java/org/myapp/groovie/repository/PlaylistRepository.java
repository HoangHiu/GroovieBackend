package org.myapp.groovie.repository;

import org.myapp.groovie.entity.playlist.Playlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PlaylistRepository extends JpaRepository<Playlist, UUID> {
    @Query("select p from Playlist p join fetch User u where u.username = :username")
    List<Playlist> findAllByUsername(String username);
}
