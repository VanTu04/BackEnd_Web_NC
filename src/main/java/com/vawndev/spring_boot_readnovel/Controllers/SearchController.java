package com.vawndev.spring_boot_readnovel.Controllers;

import com.vawndev.spring_boot_readnovel.Dto.Requests.PageRequest;
import com.vawndev.spring_boot_readnovel.Dto.Responses.ApiResponse;
import com.vawndev.spring_boot_readnovel.Dto.Responses.PageResponse;
import com.vawndev.spring_boot_readnovel.Dto.Responses.Story.StoriesResponse;
import com.vawndev.spring_boot_readnovel.Services.SearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/search")
@RequiredArgsConstructor
public class SearchController {
    private final SearchService searchService;
    @GetMapping("")
    public ApiResponse<PageResponse<StoriesResponse>> searchChapters(
            @RequestParam(required = false) String keyword,
            @RequestParam int page,
            @RequestParam int limit,
            @RequestParam(required = false) Set<String> filterFields
    ) {
        if (filterFields == null || filterFields.isEmpty()) {
            filterFields = Set.of("title");
        }

        PageResponse<StoriesResponse> result = searchService.searchStory(keyword, page, limit, filterFields);
        return ApiResponse.<PageResponse<StoriesResponse>>builder()
                .message("Successfully")
                .result(result)
                .build();
    }
    @GetMapping("/story_cate/{id}")
    public ApiResponse<PageResponse<StoriesResponse>> searchStoryByCate(
            @PathVariable(required = false) String id,
            @RequestParam int page,
            @RequestParam int limit
                ) {

        PageResponse<StoriesResponse> result = searchService.cateStory(id, page, limit);
        return ApiResponse.<PageResponse<StoriesResponse>>builder()
                .message("Successfully")
                .result(result)
                .build();
    }

}
