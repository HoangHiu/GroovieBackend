package org.myapp.groovie.repository;

import org.myapp.groovie.entity.album.Album;
import org.myapp.groovie.entity.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AlbumRepository extends JpaRepository<Album, UUID>, JpaSpecificationExecutor<Album> {
    Page<Album> findAllByUser(User user, Pageable pageable);
}
