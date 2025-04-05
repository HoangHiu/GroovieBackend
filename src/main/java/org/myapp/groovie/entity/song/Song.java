package org.myapp.groovie.entity.song;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.myapp.groovie.dto.in.SongDtoIn;
import org.myapp.groovie.entity.album.Album;
import org.myapp.groovie.entity.playlist.Playlist;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
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

    @Column(name = "languages")
    String languages;

    @Column(name = "play_count")
    long playCount;

//    @ManyToMany
//    @JoinTable(
//            name = "featured_artists",
//            joinColumns = @JoinColumn(name = "your_entity_id"),
//            inverseJoinColumns = @JoinColumn(name = "artist_id")
//    )
//    List<User> featuredArtists;


//    Foreign relations
//    Genre
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "song_genre_relations",
            joinColumns = @JoinColumn(name = "song_uuid"),
            inverseJoinColumns = @JoinColumn(name = "genre_uuid"))
    @JsonManagedReference
    Set<Genre> genres;
//Playlist
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "song_playlist_relations",
            joinColumns = @JoinColumn(name = "song_uuid"),
            inverseJoinColumns = @JoinColumn(name = "playlist_uuid"))
    @JsonBackReference
    Set<Playlist> playlists;
//Album
    @ManyToOne
    @JoinColumn(name = "album_uuid")
    @JsonBackReference
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

    public void addToGenres(Set<Genre> genres){
        this.genres = genres;
        genres.stream().map(g -> g.getSongs().add(this));
    }

    public void removeFromAlbum(Album album){
        album.getSongs().remove(this);
    }

    public void removeFromGenres(Set<Genre> genres){
        genres.stream().map(g -> g.getSongs().remove(this));
    }

    public void removeAllGenres(){
        if(!this.getGenres().isEmpty()){
            for(Genre genre : new HashSet<>(this.genres)){
                genre.getSongs().remove(this);
            }
            this.getGenres().clear();
        }
    }

    public void removeAlbum(){
        if(this.album != null){
            this.album.getSongs().remove(this);
        }
        this.album = null;
    }

    public void getValuesFromDto(SongDtoIn songDtoIn){
        this.setTitle(songDtoIn.getTitle());
        this.setDuration(songDtoIn.getDuration());
        this.setFileSize(songDtoIn.getFileSize());
        this.setLanguages(songDtoIn.getLanguages());
    }

    public static Song fromSongDtoIn(SongDtoIn songDtoIn){
        return Song.builder()
                .title(songDtoIn.getTitle())
                .duration(songDtoIn.getDuration())
                .fileSize(songDtoIn.getFileSize())
                .languages(songDtoIn.getLanguages())
                .build();
    }
}
