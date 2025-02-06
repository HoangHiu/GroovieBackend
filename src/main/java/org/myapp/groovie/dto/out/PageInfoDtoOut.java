package org.myapp.groovie.dto.out;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;

import java.util.List;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@Getter
@Setter
@AllArgsConstructor
public class PageInfoDtoOut<T> {
    int currentPage;
    int totalPages;
    long totalItems;
    int itemPerPage;
    List<T> content;

    public static <T> PageInfoDtoOut<T> fromPage(Page<T> page) {
        return new PageInfoDtoOut<>(
                page.getNumber() + 1,
                page.getTotalPages(),
                page.getTotalElements(),
                page.getSize(),
                page.getContent()
        );
    }
}
