package com.vawndev.spring_boot_readnovel.Specification;

import com.vawndev.spring_boot_readnovel.Entities.Story;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StorySpecification {
    public static Specification<Story> searchAndFilter(String keyword, Set<String> filterFields) {
        return (root, query, criteriaBuilder) -> {
            String pattern = keyword != null && !keyword.trim().isEmpty() ? "%" + keyword.toLowerCase() + "%" : null;

            // Map định nghĩa các điều kiện lọc dựa vào filterFields
            Map<String, BiFunction<String, Predicate[], Predicate>> filterMap = Map.of(
                    "category", (k, p) -> {
                        Join<Object, Object> categoryJoin = root.join("categories", JoinType.LEFT);
                        return criteriaBuilder.like(criteriaBuilder.lower(categoryJoin.get("name")), k);
                    },
                    "type", (k, p) -> criteriaBuilder.like(criteriaBuilder.lower(root.get("type").as(String.class)), k),
                    "status", (k, p) -> criteriaBuilder.like(criteriaBuilder.lower(root.get("status")), k),
                    "author", (k, p) -> {
                        Join<Object, Object> authorJoin = root.join("author", JoinType.LEFT);
                        return criteriaBuilder.like(criteriaBuilder.lower(authorJoin.get("fullName")), k);
                    }
            );

            // Create a Predicate list from the passed filters
            List<Predicate> predicates = Stream.concat(
                    // Tìm kiếm theo title nếu có keyword
                    pattern != null ? Stream.of(criteriaBuilder.like(criteriaBuilder.lower(root.get("title")), pattern)) : Stream.empty(),

                    // Lọc theo các trường hợp định nghĩa trong filterMap
                    filterFields.stream()
                            .map(field -> filterMap.containsKey(field) ? filterMap.get(field).apply(pattern, new Predicate[]{}) : null)
                            .filter(predicate -> predicate != null)
            ).collect(Collectors.toList());

            // Nếu không có điều kiện nào hợp lệ, trả về tất cả ữ liệu
            return predicates.isEmpty() ? criteriaBuilder.conjunction() : criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }



}
