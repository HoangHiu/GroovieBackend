package org.myapp.groovie.entity.playlist;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.myapp.groovie.dto.in.PLaylistDtoIn;
import org.myapp.groovie.entity.song.Song;
import org.myapp.groovie.entity.user.User;

import java.sql.Timestamp;
import java.util.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "playlists")
public class Playlist {
    @Id
    @Column(name = "uuid")
    UUID uuid;

    @Column(name = "name")
    String name;

    @Column(name = "description")
    String description;

    @JsonManagedReference
    @ManyToMany
    @JoinTable(
            name = "song_playlist_relations",
            joinColumns = @JoinColumn(name = "playlist_uuid"),
            inverseJoinColumns = @JoinColumn(name = "song_uuid")
    )
    @OrderColumn(name = "song_order")
    List<Song> songs = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "user_uuid")
    @JsonBackReference
    User user;

    @Column(name = "created_at")
    Timestamp createdAt;

    @Column(name = "updated_at")
    Timestamp updatedAt;

    public static Playlist fromDtoIn(PLaylistDtoIn pLaylistDtoIn){
        return Playlist.builder()
                .name(pLaylistDtoIn.getName())
                .description(pLaylistDtoIn.getDescription())
                .build();
    }
}
