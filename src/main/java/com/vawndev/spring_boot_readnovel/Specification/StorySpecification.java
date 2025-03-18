package com.vawndev.spring_boot_readnovel.Specification;

import com.vawndev.spring_boot_readnovel.Entities.Story;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class StorySpecification {
    public static Specification<Story> searchByFilter(String keyword) {
        return (root, query, criteriaBuilder) -> {
            if (keyword == null || keyword.trim().isEmpty()) {
                return criteriaBuilder.conjunction(); // Trả về tất cả nếu không có keyword
            }

            String[] keywords = keyword.trim().toLowerCase().split("\\s+");
            List<Predicate> predicates = new ArrayList<>();

            // LEFT JOIN với categories (chỉ JOIN một lần)
            Join<Object, Object> categoryJoin = root.join("categories", JoinType.LEFT);

            for (String word : keywords) {
                String pattern = "%" + word + "%";

                predicates.add(criteriaBuilder.or(
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("title")), pattern),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("description")), pattern),
                        criteriaBuilder.like(criteriaBuilder.lower(categoryJoin.get("name")), pattern),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("type").as(String.class)), pattern),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("status")), pattern)
                ));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0])); // Phải khớp tất cả từ khóa
        };
    }



    public static Specification<Story> searchByKeyword(String keyword) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (keyword != null && !keyword.trim().isEmpty()) {
                String pattern = "%" + keyword.trim().toLowerCase() + "%";

                // Search by title
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("title")), pattern));

                // Search by type
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("type").as(String.class)), pattern));

                // Search by status
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("status")), pattern));

                // Search by author name (LEFT JOIN)
                Join<Object, Object> authorJoin = root.join("author", JoinType.LEFT);
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(authorJoin.get("fullName")), pattern));

            }

            return criteriaBuilder.or(predicates.toArray(new Predicate[0]));
        };
    }


}
