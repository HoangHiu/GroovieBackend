package org.myapp.groovie.dto.in;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class SongSearchDtoIn {
    String keywords;
    int pageNumber;
    int pageSize;
}
