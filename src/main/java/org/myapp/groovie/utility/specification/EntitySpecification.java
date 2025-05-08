package org.myapp.groovie.utility.specification;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import org.myapp.groovie.entity.user.Role;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class EntitySpecification<T> {
    public static <T> Specification<T> titleLike(String title){
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicateList = new ArrayList<>();
            Predicate predicate;
            if(title != null){
                predicate = criteriaBuilder.like(
                        criteriaBuilder.trim(criteriaBuilder.lower(root.get("title"))),
                        "%"+title.trim().toLowerCase()+"%");
                predicateList.add(predicate);
            }

            return criteriaBuilder.and(predicateList.toArray(new Predicate[0]));
        };
    }

    public static <T> Specification<T> columnLike(String title, String userId) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicateList = new ArrayList<>();

            if (title != null && !title.isBlank()) {
                predicateList.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("title")),
                        "%" + title.trim().toLowerCase() + "%"
                ));
            }

            if (userId != null && !userId.isBlank()) {
                predicateList.add(criteriaBuilder.equal(
                        root.get("user").get("uuid"),
                        UUID.fromString(userId)
                ));
            }

            return criteriaBuilder.and(predicateList.toArray(new Predicate[0]));
        };
    }


    public static <T> Specification<T> userInfoNameLike(String displayName){
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicateList = new ArrayList<>();
            Predicate predicate = criteriaBuilder.like(
                    criteriaBuilder.trim(criteriaBuilder.lower(root.get("name"))),
                    "%"+displayName.trim().toLowerCase()+"%");
            predicateList.add(predicate);
            return criteriaBuilder.and(predicateList.toArray(new Predicate[0]));
        };
    }

    public static <T> Specification<T> userHasRole(Role role) {
        return (root, query, cb) -> {
            // Join from PersonalDetail → User
            Join<Object, Object> userJoin = root.join("user");

            // Join from User → groups
            Join<Object, Object> groupJoin = userJoin.join("groups");

            query.distinct(true);

            return cb.equal(groupJoin.get("role"), role);
        };
    }
}
