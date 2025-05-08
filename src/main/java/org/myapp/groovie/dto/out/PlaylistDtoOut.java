package org.myapp.groovie.dto.out;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import lombok.*;
import org.myapp.groovie.entity.playlist.Playlist;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PlaylistDtoOut {
    UUID uuid;
    String name;
    String description;
    List<SongDtoOut> songs;
    List<String> coverUrls;

    public static PlaylistDtoOut fromPlaylist(Playlist playlist){
        return PlaylistDtoOut.builder()
                .uuid(playlist.getUuid())
                .name(playlist.getName())
                .description(playlist.getDescription())
                .songs(playlist.getSongs().stream().map(
                        s -> SongDtoOut.fromSong(s, "")
                ).toList())
                .build();
    }

    public static PlaylistDtoOut fromPlaylistWoSongs(Playlist playlist, List<String> coverUrls){
        return PlaylistDtoOut.builder()
                .uuid(playlist.getUuid())
                .name(playlist.getName())
                .description(playlist.getDescription())
                .coverUrls(coverUrls)
                .build();
    }
}
