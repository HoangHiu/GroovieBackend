package org.myapp.groovie.dto.out;

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

@Builder
@AllArgsConstructor
@Getter
@Setter
public class SongDtoOut {
    Song song;
    String url;

    public static SongDtoOut fromSong(Song song, String url){
        return SongDtoOut.builder()
                .song(song)
                .url(url)
                .build();
    }
}
