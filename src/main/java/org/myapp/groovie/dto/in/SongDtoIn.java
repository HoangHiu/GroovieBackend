package org.myapp.groovie.dto.in;

import jakarta.persistence.Column;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SongDtoIn {
    String title;
    int duration;
    float fileSize;
    String languages;

    Set<String> genreIds;
    String albumId;
}
