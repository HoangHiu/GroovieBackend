package org.myapp.groovie.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AddSongsToPlaylistRequest {
    String playlistId;
    List<String> songIds;
}
