package com.vawndev.spring_boot_readnovel.Specification;

import com.vawndev.spring_boot_readnovel.Entities.Story;
import jakarta.persistence.criteria.Predicate;
import lombok.Data;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

@Data
public class StorySpecification {
    public static Specification<Story> searchByKeyword(String keyword) {
        return ((root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (keyword != null && !keyword.isEmpty()) {
                predicates.add(criteriaBuilder.like(root.get("title"), "%" + keyword + "%"));
                predicates.add(criteriaBuilder.like(root.get("categories"), "%" + keyword + "%"));
                predicates.add(criteriaBuilder.like(root.get("description"), "%" + keyword + "%"));
                predicates.add(criteriaBuilder.like(root.get("author"), "%" + keyword + "%"));
                predicates.add(criteriaBuilder.like(root.get("type"), "%" + keyword + "%"));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
        });
    }
}
