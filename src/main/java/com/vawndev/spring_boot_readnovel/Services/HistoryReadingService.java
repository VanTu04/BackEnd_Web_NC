package com.vawndev.spring_boot_readnovel.Services;

import com.vawndev.spring_boot_readnovel.Dto.Requests.ConditionRequest;
import com.vawndev.spring_boot_readnovel.Dto.Requests.PageRequest;
import com.vawndev.spring_boot_readnovel.Dto.Responses.Chapter.ChapterResponsePurchase;
import com.vawndev.spring_boot_readnovel.Dto.Responses.My.ReadingHistoryResponse;
import com.vawndev.spring_boot_readnovel.Dto.Responses.PageResponse;

public interface HistoryReadingService {
    PageResponse<ReadingHistoryResponse> getHistory(String bearerToken, PageRequest req);
    void saveHistory(String bearerToken,String chapter_id);
    void deleteHistory(String bearerToken,String story_id);
    void deleteAllHistory(String bearerToken);
}
