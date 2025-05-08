package com.vawndev.spring_boot_readnovel.Services;

import com.vawndev.spring_boot_readnovel.Dto.Responses.PageResponse;
import com.vawndev.spring_boot_readnovel.Dto.Responses.Story.StoriesResponse;

import java.util.Map;

public interface SearchService {
    PageResponse<StoriesResponse> searchStory(int page, int limit, Map<String, String> filterFields);

    PageResponse<StoriesResponse> cateStory(String keyword, int page, int limit);

    PageResponse<StoriesResponse> elasticSearchStory(String keyword, int page, int limit);
}
