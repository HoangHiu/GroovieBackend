package org.myapp.groovie.entity.song;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.myapp.groovie.entity.album.Album;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "songs")
public class Song {
    @Id
    @Column(name = "uuid")
    UUID uuid;

    @Column(name = "title")
    String title;

    @Column(name = "duration")
    int duration;

    @Column(name = "file_size")
    float fileSize;

    @Column(name = "language")
    String language;

    @Column(name = "play_count")
    long playCount;

//    Foreign relations
//    Genre
    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinTable(name = "song_genre_relations",
            joinColumns = @JoinColumn(name = "song_uuid"),
            inverseJoinColumns = @JoinColumn(name = "genre_uuid"))
    @JsonManagedReference
    Set<Genre> genres;
//Album
    @ManyToOne
    @JoinColumn(name = "album_uuid")
    Album album;

//  Creation stuff
    @Column(name = "created_at")
    Timestamp createdAt;

    @Column(name = "updated_at")
    Timestamp updatedAt;

    public void addToAlbum(Album album){
        this.album = album;
        album.getSongs().add(this);
    }
}
