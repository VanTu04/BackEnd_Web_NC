package com.vawndev.spring_boot_readnovel.Specification;

import com.vawndev.spring_boot_readnovel.Entities.Story;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

public class StorySpecification {
    public static Specification<Story> searchAndFilter(Map<String, String> filterFields) {
        return (root, query, criteriaBuilder) -> {

            List<Predicate> predicates = new ArrayList<>();

            Map<String, BiFunction<String, Predicate[], Predicate>> filterMap = Map.of(
                    "keyword",
                    (k, p) -> criteriaBuilder.like(criteriaBuilder.lower(root.get("title").as(String.class)),
                            "%" + k.toLowerCase() + "%"),
                    "category", (k, p) -> {
                        Join<Object, Object> categoryJoin = root.join("categories", JoinType.LEFT);
                        return criteriaBuilder.like(criteriaBuilder.lower(categoryJoin.get("id")),
                                "%" + k.toLowerCase() + "%");
                    },
                    "type",
                    (k, p) -> criteriaBuilder.like(criteriaBuilder.lower(root.get("type").as(String.class)),
                            "%" + k.toLowerCase() + "%"),
                    "status",
                    (k, p) -> criteriaBuilder.like(criteriaBuilder.lower(root.get("status")),
                            "%" + k.toLowerCase() + "%"),
                    "author", (k, p) -> {
                        Join<Object, Object> authorJoin = root.join("author", JoinType.LEFT);
                        return criteriaBuilder.like(criteriaBuilder.lower(authorJoin.get("fullName")),
                                "%" + k.toLowerCase() + "%");
                    });

            filterFields.forEach((key, value) -> {
                if (filterMap.containsKey(key) && value != null && !value.trim().isEmpty()) {
                    Predicate predicate = filterMap.get(key).apply(value, null);
                    predicates.add(predicate);
                }
            });

            return predicates.isEmpty() ? criteriaBuilder.disjunction()
                    : criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
