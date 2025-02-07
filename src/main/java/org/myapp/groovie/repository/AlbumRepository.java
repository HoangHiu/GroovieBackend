package org.myapp.groovie.repository;

import org.myapp.groovie.entity.album.Album;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AlbumRepository extends JpaRepository<Album, UUID>, JpaSpecificationExecutor<Album> {
}
