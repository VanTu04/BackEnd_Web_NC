package com.vawndev.spring_boot_readnovel.Repositories;

import com.vawndev.spring_boot_readnovel.Entities.Story;
import com.vawndev.spring_boot_readnovel.Entities.User;
import com.vawndev.spring_boot_readnovel.Enum.IS_AVAILBLE;
import com.vawndev.spring_boot_readnovel.Enum.STORY_STATUS;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StoryRepository extends JpaRepository<Story, String> {
    Optional<Story> findByIdAndAuthor(String id, User author);
    @Query("""
    SELECT s FROM Story s 
    WHERE s.isVisibility = TRUE 
    AND  s.isAvailable = :available 
    AND s.status IN :statusList
    ORDER BY s.createdAt ASC
    """)
    Page<Story> findAccepted(
            IS_AVAILBLE available,
            List<STORY_STATUS> statusList,
            Pageable pageable
    );

    @Query("""
        SELECT s FROM Story s
        WHERE s.isVisibility = TRUE 
        AND  s.isAvailable = :available 
        AND s.status IN :statusList     
        ORDER BY s.views DESC, s.rate DESC
        """)
    List<Story> findTopStories(IS_AVAILBLE available,
                               List<STORY_STATUS> statusList,
                               Pageable pageable);
    @Query("""
    SELECT s FROM Story s 
    WHERE s.isVisibility = TRUE 
    AND  s.isAvailable = :available 
    AND s.status IN :statusList
    ORDER BY s.updatedAt ASC 
    """)
    Page<Story> findUpdating(IS_AVAILBLE available,
                               List<STORY_STATUS> statusList,
                               Pageable pageable);
    Page<Story> findAll(Pageable pageable);

    @Query("select count(*) from Story s where s.isAvailable = 'ACCEPTED' and month(s.updatedAt) = :month and year(s.updatedAt) = :year")
    Long countApprovedStories(@Param("month") int month, @Param("year") int year);

    @Query("SELECT COUNT(s) FROM Story s WHERE s.isAvailable = 'REJECTED' AND MONTH(s.createdAt) = :month AND YEAR(s.createdAt) = :year")
    Long countRejectedStories(@Param("month") int month, @Param("year") int year);
}
