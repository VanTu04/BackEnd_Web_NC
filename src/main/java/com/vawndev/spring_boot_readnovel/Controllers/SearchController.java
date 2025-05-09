package com.vawndev.spring_boot_readnovel.Controllers;

import com.vawndev.spring_boot_readnovel.Dto.Responses.ApiResponse;
import com.vawndev.spring_boot_readnovel.Dto.Responses.PageResponse;
import com.vawndev.spring_boot_readnovel.Dto.Responses.Story.StoriesResponse;
import com.vawndev.spring_boot_readnovel.Services.SearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/search")
@RequiredArgsConstructor
public class SearchController {
    private final SearchService searchService;

    @GetMapping("")
    public ApiResponse<PageResponse<StoriesResponse>> searchChapters(
            @RequestParam int page,
            @RequestParam int limit,
            @RequestParam(required = false) Map<String, String> filterFields) {

        PageResponse<StoriesResponse> result = searchService.searchStory(page, limit, filterFields);
        return ApiResponse.<PageResponse<StoriesResponse>>builder()
                .message("Successfully")
                .result(result)
                .build();
    }

    @GetMapping("/elastic")
    public ApiResponse<PageResponse<StoriesResponse>> searchStoryElastic(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int limit,
            @RequestParam(required = false) String keyword
    ){
        PageResponse<StoriesResponse> result = searchService.elasticSearchStory(keyword, page, limit);
        return ApiResponse.<PageResponse<StoriesResponse>>builder()
                .message("Successfully")
                .result(result)
                .build();
    }

    @GetMapping("/story_cate/{id}")
    public ApiResponse<PageResponse<StoriesResponse>> searchStoryByCate(
            @PathVariable(required = false) String id,
            @RequestParam int page,
            @RequestParam int limit) {

        PageResponse<StoriesResponse> result = searchService.cateStory(id, page, limit);
        return ApiResponse.<PageResponse<StoriesResponse>>builder()
                .message("Successfully")
                .result(result)
                .build();
    }

}
