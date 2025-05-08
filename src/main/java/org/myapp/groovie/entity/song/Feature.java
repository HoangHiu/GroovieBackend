package org.myapp.groovie.entity.song;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.myapp.groovie.entity.song.Song;

import java.util.UUID;

@Entity
@Table(name = "song_specs")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Feature {
    @Id
    @GeneratedValue
    @Column(name = "uuid")
    private UUID uuid;

    @OneToOne
    @JoinColumn(name = "song_id", nullable = false, unique = true)
    private Song song;

    @Column(name = "duration")
    private Float duration;

    @Column(name = "tempo")
    private Float tempo;

    @Column(name = "spectral_centroid")
    private Float spectral_centroid;

    @Column(name = "chromagram")
    private Float chromagram;

    @Column(name = "rms")
    private Float rms;
}
