package com.vawndev.spring_boot_readnovel.Repositories;

import com.vawndev.spring_boot_readnovel.Entities.Chapter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChapterRepository extends JpaRepository<Chapter, String> {
    List<Chapter> findAllByStoryId(String storyId);
    @Query("SELECT c FROM Chapter c " +
            "JOIN Story s ON c.story.id = s.id " +
            "JOIN User u ON u.id = s.author.id " +
            "WHERE c.id = :chapterId AND u.id = :userId")
    Optional<Chapter> findByIdAndAuthorId( String chapterId,String userId);
}
