package org.myapp.groovie.dto.out;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.myapp.groovie.entity.album.Album;
import org.myapp.groovie.entity.song.Genre;
import org.myapp.groovie.entity.song.Song;

import java.sql.Timestamp;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Builder
@AllArgsConstructor
@Getter
@Setter
public class SongDtoOut {
    UUID uuid;
    String title;
    int duration;
    float fileSize;
    String languages;
    long playCount;
    String url;
    Set<String> genres;
    AlbumDtoOut albumDtoOut;

    public static SongDtoOut fromSong(Song song, String url){
        Set<String> genreList = song.getGenres().stream().map(Genre::getName).collect(Collectors.toSet());
        String albumName = song.getAlbum().getTitle();

        return SongDtoOut.builder()
                .uuid(song.getUuid())
                .title(song.getTitle())
                .duration(song.getDuration())
                .fileSize(song.getFileSize())
                .languages(song.getLanguages())
                .playCount(song.getPlayCount())
                .genres(genreList)
                .albumDtoOut(AlbumDtoOut.fromAlbum(song.getAlbum(), ""))
                .url(url)
                .build();
    }
}
