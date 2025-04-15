package com.vawndev.spring_boot_readnovel.Repositories;

import com.vawndev.spring_boot_readnovel.Entities.Chapter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChapterRepository extends JpaRepository<Chapter, String> {
    @Query("SELECT c FROM Chapter c WHERE c.story.id = :storyId ORDER BY c.createdAt DESC")
    Page<Chapter> findAllByStoryId(String storyId, Pageable pageable);
    @Query("SELECT c FROM Chapter c " +
            "JOIN Story s ON c.story.id = s.id " +
            "JOIN User u ON u.id = s.author.id " +
            "WHERE c.id = :chapterId AND u.id = :userId")
    Optional<Chapter> findByIdAndAuthorId( String chapterId,String userId);

    @Query("SELECT c.id FROM Chapter c WHERE c.createdAt > (SELECT ch.createdAt FROM Chapter ch WHERE ch.id = :chapterId) ORDER BY c.createdAt ASC")
    List<String> findNextChapter(String chapterId, Pageable pageable);

    @Query("SELECT c.id FROM Chapter c WHERE c.createdAt < (SELECT ch.createdAt FROM Chapter ch WHERE ch.id = :chapterId) ORDER BY c.createdAt DESC")
    List<String> findPrevChapter(String chapterId, Pageable pageable);


}
