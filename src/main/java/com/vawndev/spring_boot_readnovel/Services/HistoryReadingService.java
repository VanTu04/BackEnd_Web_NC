package com.vawndev.spring_boot_readnovel.Services;

import com.vawndev.spring_boot_readnovel.Dto.Requests.ConditionRequest;
import com.vawndev.spring_boot_readnovel.Dto.Requests.PageRequest;
import com.vawndev.spring_boot_readnovel.Dto.Responses.Chapter.ChapterResponsePurchase;
import com.vawndev.spring_boot_readnovel.Dto.Responses.Chapter.ChaptersResponse;
import com.vawndev.spring_boot_readnovel.Dto.Responses.My.ReadingHistoryResponse;
import com.vawndev.spring_boot_readnovel.Dto.Responses.PageResponse;
import com.vawndev.spring_boot_readnovel.Entities.User;

import java.util.List;
import java.util.Set;

public interface HistoryReadingService {
    PageResponse<ReadingHistoryResponse> getHistory(PageRequest req);

    Set<String> getChaptersIdHistory(String storyId, User currentUser);

    void saveHistory(String chapter_id);

    void deleteHistory(String story_id);

    void deleteAllHistory();

    ChaptersResponse getLatestChapter(String story_id);
}
