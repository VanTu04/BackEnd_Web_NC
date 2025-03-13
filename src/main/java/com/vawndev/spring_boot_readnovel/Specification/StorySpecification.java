package com.vawndev.spring_boot_readnovel.Specification;

import com.vawndev.spring_boot_readnovel.Entities.Story;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class StorySpecification {
    public static Specification<Story> searchByKeyword(String keyword) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (keyword != null && !keyword.trim().isEmpty()) {
                String pattern = "%" + keyword.trim().toLowerCase() + "%";

                // Search by title
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("title")), pattern));

                // Search by description
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("description")), pattern));

                // Search by category name (use LEFT JOIN)
                Join<Object, Object> categoryJoin = root.join("categories", JoinType.LEFT);
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(categoryJoin.get("name")), pattern));

                // Search by author name (use JOIN)
                Join<Object, Object> authorJoin = root.join("author", JoinType.LEFT);
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(authorJoin.get("fullName")), pattern));

                // Search by type (because type is Enum so need convert to String)
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("type").as(String.class)), pattern));
            }

            return criteriaBuilder.or(predicates.toArray(new Predicate[0]));
        };
    }
}
