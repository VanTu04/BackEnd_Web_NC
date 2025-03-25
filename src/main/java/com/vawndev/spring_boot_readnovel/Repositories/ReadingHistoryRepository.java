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

import java.util.List;

public interface ReadingHistoryRepository extends JpaRepository<ReadingHistory, String> {

    Page<ReadingHistory> findByUser(User user, Pageable pageable);

    ReadingHistory findByUserAndChapter(User user, Chapter chapter);

    @Query("SELECT rh FROM ReadingHistory rh WHERE rh.user = :user AND rh.chapter.story = :story")
    ReadingHistory findByUserAndStory( User user, Story story);

    @Modifying
    @Query("DELETE FROM ReadingHistory r WHERE r.user.id = :userId")
    void deleteByUserId(String userId);

}
