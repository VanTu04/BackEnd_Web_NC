package com.vawndev.spring_boot_readnovel.Repositories;

import com.vawndev.spring_boot_readnovel.Entities.Chapter;
import com.vawndev.spring_boot_readnovel.Entities.ReadingHistory;
import com.vawndev.spring_boot_readnovel.Entities.Story;
import com.vawndev.spring_boot_readnovel.Entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ReadingHistoryRepository extends JpaRepository<ReadingHistory, String> {

        @Modifying
        @Query("DELETE FROM ReadingHistory rh WHERE rh.chapter.id = :id_chapter")
        void deleteAllByChapterId(String id_chapter);

        @Query("SELECT rh FROM ReadingHistory rh WHERE rh.chapter.id =:id_chapter")
        List<ReadingHistory> findByChapters(String id_chapter);

        Page<ReadingHistory> findByUser(User user, Pageable pageable);

        ReadingHistory findByUserAndChapter(User user, Chapter chapter);

        @Query("SELECT rh FROM ReadingHistory rh WHERE rh.user = :user AND rh.chapter.story = :story")
        ReadingHistory findByUserAndStory(User user, Story story);

        @Modifying
        @Query("DELETE FROM ReadingHistory r WHERE r.user.id = :userId")
        void deleteByUserId(String userId);

        @Query("SELECT hr.chapter FROM ReadingHistory hr " +
                        "JOIN hr.chapter c " +
                        "WHERE c.story.id = :storyId AND hr.user.id = :userId " +
                        "ORDER BY hr.createdAt DESC")
        Optional<Chapter> findLatestChapter(String storyId, String userId);

        @Query("SELECT hr.chapter FROM ReadingHistory hr " +
                        "WHERE hr.chapter.story.id = :storyId AND hr.user.id = :userId " +
                        "ORDER BY hr.createdAt DESC")
        List<Chapter> findReadingChapters(String storyId,
                        String userId);

        @Query("SELECT hr.chapter FROM ReadingHistory hr " +
                        "WHERE hr.user.id = :userId " +
                        "ORDER BY hr.createdAt DESC")
        List<Chapter> findChapters(
                        String userId);

        @Query("SELECT c FROM Chapter c WHERE c.story.id = :storyId ORDER BY c.createdAt ASC")
        Optional<Chapter> findFirstChapterByStoryId(String storyId);

}
