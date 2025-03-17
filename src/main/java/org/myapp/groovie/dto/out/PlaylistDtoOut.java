package org.myapp.groovie.dto.out;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import lombok.*;
import org.myapp.groovie.entity.playlist.Playlist;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PlaylistDtoOut {
    UUID uuid;
    String name;
    String description;

    public static PlaylistDtoOut fromPlaylist(Playlist playlist){
        return PlaylistDtoOut.builder()
                .uuid(playlist.getUuid())
                .name(playlist.getName())
                .description(playlist.getDescription())
                .build();
    }
}
