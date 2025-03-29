package com.vawndev.spring_boot_readnovel.Repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.vawndev.spring_boot_readnovel.Entities.ReadingHistory;

@Repository
public interface ReadingHistoryRepository extends JpaRepository<ReadingHistory, String> {
    Optional<ReadingHistory> findByUserIdAndStoryId(String userId, String storyId);
    List<ReadingHistory> findByUserId(String userId);
}