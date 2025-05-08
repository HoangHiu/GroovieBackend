package org.myapp.groovie.repository;

import org.myapp.groovie.entity.song.Song;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Repository
public interface SongRepository extends JpaRepository<Song, UUID>, JpaSpecificationExecutor<Song> {
    List<Song> findAllByUuidIn(List<UUID> songIds);

    @Modifying
    @Transactional
    @Query(value = """
    WITH deleted_relations AS (
        DELETE FROM song_playlist_relations WHERE song_uuid = :songId
    ),deleted_genre_relations AS (
        DELETE FROM song_genre_relations WHERE song_uuid = :songId
    ), deleted_specs AS (
        DELETE FROM song_specs WHERE song_id = :songId
    )
    DELETE FROM songs WHERE uuid = :songId
    """, nativeQuery = true)
    void deleteSongAndRelations(@Param("songId") UUID songId);
}
