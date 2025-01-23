package org.myapp.groovie.entity.song;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.sql.Timestamp;
import java.util.Set;
import java.util.UUID;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "genres")
public class Genre {
    @Id
    @Column(name = "uuid")
    UUID uuid;

    @Column(name = "name", length = 24)
    String name;

    @Column(name = "slug")
    String slug;

    @Column(name = "description")
    String description;

//    relation
    @ManyToMany(mappedBy = "genres", fetch = FetchType.LAZY)
    @JsonBackReference
    Set<Song> songs;

//   System
    @Column(name = "created_at")
    Timestamp createdAt;

    @Column(name = "updated_at")
    Timestamp updatedAt;

    @Column(name = "created_by")
    UUID createdBy;

    @Column(name = "updated_by")
    UUID updatedBy;
}
