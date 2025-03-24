package com.vawndev.spring_boot_readnovel.Services;

import com.vawndev.spring_boot_readnovel.Dto.Requests.PageRequest;
import com.vawndev.spring_boot_readnovel.Dto.Responses.PageResponse;
import com.vawndev.spring_boot_readnovel.Dto.Responses.Story.StoriesResponse;

import java.util.Set;


public interface SearchService {
    PageResponse<StoriesResponse> searchStory(String keyword, int page, int limit, Set<String> filterFields) ;
}
