package com.vawndev.spring_boot_readnovel.Repositories;

import com.vawndev.spring_boot_readnovel.Entities.Chapter;
import com.vawndev.spring_boot_readnovel.Entities.Story;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface ChapterRepository extends JpaRepository<Chapter, String> {
    @Query("""
                SELECT c.id FROM Chapter c
                WHERE c.story.id = :storyId
            """)

    List<String> findChapterIdByStory(@Param("storyId") String storyId);

    @Query("""
                SELECT c.price FROM Chapter c
                WHERE c.story.id = :storyId
            """)

    List<BigDecimal> findChaptersByStory(@Param("storyId") String storyId);

    @Query("SELECT c FROM Chapter c WHERE c.story.id = :storyId ORDER BY c.createdAt DESC")
    Page<Chapter> findAllByStoryId(String storyId, Pageable pageable);

    @Query("SELECT c FROM Chapter c " +
            "JOIN Story s ON c.story.id = s.id " +
            "JOIN User u ON u.id = s.author.id " +
            "WHERE c.id = :chapterId AND u.id = :userId")
    Optional<Chapter> findByIdAndAuthorId(String chapterId, String userId);

    @Query("""
                SELECT c.id FROM Chapter c
                WHERE c.story.id = (
                    SELECT ch.story.id FROM Chapter ch WHERE ch.id = :chapterId
                ) AND c.createdAt > (
                    SELECT ch.createdAt FROM Chapter ch WHERE ch.id = :chapterId
                )
                ORDER BY c.createdAt ASC
            """)
    List<String> findNextChapter(@Param("chapterId") String chapterId, Pageable pageable);

    @Query("""
                SELECT c.id FROM Chapter c
                WHERE c.story.id = (
                    SELECT ch.story.id FROM Chapter ch WHERE ch.id = :chapterId
                ) AND c.createdAt < (
                    SELECT ch.createdAt FROM Chapter ch WHERE ch.id = :chapterId
                )
                ORDER BY c.createdAt DESC
            """)
    List<String> findPrevChapter(@Param("chapterId") String chapterId, Pageable pageable);

    @Query("SELECT c FROM Chapter c WHERE c.id =:id")
    Optional<Chapter> findByIdChapter(String id);

}
