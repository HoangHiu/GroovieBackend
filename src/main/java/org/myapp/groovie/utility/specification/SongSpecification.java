package org.myapp.groovie.utility.specification;

import jakarta.persistence.criteria.Predicate;
import org.myapp.groovie.entity.song.Song;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;


public class SongSpecification {
    public static Specification<Song> titleLike(String title){
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicateList = new ArrayList<>();
            Predicate predicate = criteriaBuilder.like(
                    criteriaBuilder.trim(criteriaBuilder.lower(root.get("title"))),
                    "%"+title.trim().toLowerCase()+"%");
            predicateList.add(predicate);
            return criteriaBuilder.and(predicateList.toArray(new Predicate[0]));
        };
    }
}
