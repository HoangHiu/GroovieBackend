package org.myapp.groovie.dto.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.myapp.groovie.entity.album.Album;
import org.myapp.groovie.entity.song.Song;

@Builder
@AllArgsConstructor
@Getter
@Setter
public class AlbumDtoOut {
    Album album;
    String url;

    public static AlbumDtoOut fromAlbum(Album album, String url){
        return AlbumDtoOut.builder()
                .album(album)
                .url(url)
                .build();
    }
}
