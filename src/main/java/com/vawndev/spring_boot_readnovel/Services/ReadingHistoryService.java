package com.vawndev.spring_boot_readnovel.Services;

import java.util.List;

import com.vawndev.spring_boot_readnovel.Entities.ReadingHistory;

public interface ReadingHistoryService {
    void saveReadingHistory(String userId, String storyId, String chapterId);
    List<ReadingHistory> getReadingHistory(String userId);
}