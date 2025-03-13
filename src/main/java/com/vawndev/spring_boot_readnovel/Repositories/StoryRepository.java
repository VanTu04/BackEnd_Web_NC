package com.vawndev.spring_boot_readnovel.Repositories;

import com.vawndev.spring_boot_readnovel.Entities.Story;
import com.vawndev.spring_boot_readnovel.Entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StoryRepository extends JpaRepository<Story, String> {
    Page<Story> findAllByIsApprovedAndIsAvailableAndDeleteAtIsNull(boolean isApproved, boolean isAvailable , Pageable pageable);
    Optional<Story> findByIdAndAuthor(String id, User author);
    Page<Story> findAll(Pageable pageable);
    Page<Story> findAllByIsApprovedAndIsAvailableAndDeleteAtIsNotNull(boolean isApproved, boolean isAvailable,Pageable pageable);


    @Query("select count(*) from Story s where s.isApproved = true and month(s.updatedAt) = :month and year(s.updatedAt) = :year")
    Long countApprovedStories(@Param("month") int month, @Param("year") int year);

    @Query("SELECT COUNT(s) FROM Story s WHERE s.isApproved = false AND MONTH(s.createdAt) = :month AND YEAR(s.createdAt) = :year")
    Long countRejectedStories(@Param("month") int month, @Param("year") int year);
}
