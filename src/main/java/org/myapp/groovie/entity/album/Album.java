package org.myapp.groovie.entity.album;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.myapp.groovie.dto.in.AlbumDtoIn;
import org.myapp.groovie.entity.song.Song;

import java.sql.Timestamp;
import java.util.Set;
import java.util.UUID;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "albums")
@Builder
public class Album {
    @Id
    @Column(name = "uuid")
    UUID uuid;

    @Column(name = "title")
    String title;

    @Column(name = "description")
    String description;

    @Column(name = "total_duration")
    int totalDuration;

    @Column(name = "release_year")
    int releaseYear;

    //Relation
    //Song
    @OneToMany(cascade = CascadeType.REMOVE, mappedBy = "album", fetch = FetchType.EAGER)
    @JsonManagedReference
    Set<Song> songs;

    //  Creation stuff
    @Column(name = "created_at")
    Timestamp createdAt;

    @Column(name = "updated_at")
    Timestamp updatedAt;

    public static Album fromDto(AlbumDtoIn albumDtoIn){
        return Album.builder()
                .title(albumDtoIn.getTitle())
                .description(albumDtoIn.getDescription())
                .totalDuration(albumDtoIn.getTotalDuration())
                .releaseYear(albumDtoIn.getReleaseYear())
                .build();
    }
}
