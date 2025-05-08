package org.myapp.groovie.repository;

import org.myapp.groovie.entity.playlist.Playlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PlaylistRepository extends JpaRepository<Playlist, UUID> {
    @Query("SELECT p FROM Playlist p LEFT JOIN FETCH p.user u WHERE u.username = :username")
    List<Playlist> findAllByUsername(String username);
}
