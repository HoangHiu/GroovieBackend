package org.myapp.groovie.dto.out;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.myapp.groovie.entity.album.Album;
import org.myapp.groovie.entity.song.Song;

import java.util.UUID;

@Builder
@AllArgsConstructor
@Getter
@Setter
public class AlbumDtoOut {
    UUID uuid;
    String title;
    String description;
    int totalDuration;
    int releaseYear;

    UserDtoOut userDtoOut;

    String url;

    public static AlbumDtoOut fromAlbum(Album album, String url){
        return AlbumDtoOut.builder()
                .uuid(album.getUuid())
                .title(album.getTitle())
                .description(album.getDescription())
                .totalDuration(album.getTotalDuration())
                .releaseYear(album.getReleaseYear())
                .userDtoOut(UserDtoOut.fromUser(album.getUser()))
                .url(url)
                .build();
    }
}
