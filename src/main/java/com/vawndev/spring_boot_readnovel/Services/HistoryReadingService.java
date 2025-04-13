package com.vawndev.spring_boot_readnovel.Services;

import com.vawndev.spring_boot_readnovel.Dto.Requests.PageRequest;
import com.vawndev.spring_boot_readnovel.Dto.Responses.My.ReadingHistoryResponse;
import com.vawndev.spring_boot_readnovel.Dto.Responses.PageResponse;

public interface HistoryReadingService {
    PageResponse<ReadingHistoryResponse> getHistory( PageRequest req);
    ReadingHistoryResponse getLatestHistory();
    void saveHistory(String chapter_id);
    void deleteHistory(String story_id);
    void deleteAllHistory();

}
