package org.myapp.groovie.repository;

import jakarta.transaction.Transactional;
import org.myapp.groovie.entity.album.Album;
import org.myapp.groovie.entity.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AlbumRepository extends JpaRepository<Album, UUID>, JpaSpecificationExecutor<Album> {
    Page<Album> findAllByUser(User user, Pageable pageable);

    @Modifying
    @Transactional
    @Query(value = """
    WITH deleted_relations AS (
        DELETE FROM song_playlist_relations WHERE song_uuid IN (SELECT uuid FROM songs WHERE album_uuid = :albumId)
    ),deleted_genre_relations AS (
        DELETE FROM song_genre_relations WHERE song_uuid IN (SELECT uuid FROM songs WHERE album_uuid = :albumId)
    ), deleted_specs AS (
        DELETE FROM song_specs WHERE song_id IN (SELECT uuid FROM songs WHERE album_uuid = :albumId)
    )
    DELETE FROM songs WHERE album_uuid = :albumId;
    DELETE FROM albums WHERE uuid = :albumId
    """, nativeQuery = true)
    void deleteAllSongsAndAlbum(@Param("albumId") UUID albumId);
}
