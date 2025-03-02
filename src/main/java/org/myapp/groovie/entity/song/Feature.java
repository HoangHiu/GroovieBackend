package org.myapp.groovie.entity.song;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.myapp.groovie.entity.song.Song;

import java.util.UUID;

@Entity
@Table(name = "song_features")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Feature {
    @Id
    @Column(name = "uuid")
    private UUID uuid;

    @OneToOne
    @JoinColumn(name = "song_id", nullable = false, unique = true)
    private Song song;

    @Column(name = "tempo")
    private Float tempo;

    @Column(name = "key_signature")
    private Integer keySignature;

    @Column(name = "mode")
    private Integer mode;

    @Column(name = "danceability")
    private Float danceability;

    @Column(name = "energy")
    private Float energy;

    @Column(name = "valence")
    private Float valence;

    @Column(name = "instrumentalness")
    private Float instrumentalness;

    @Column(name = "loudness")
    private Float loudness;

    @Column(name = "speechiness")
    private Float speechiness;

    @Column(name = "acousticness")
    private Float acousticness;
}
