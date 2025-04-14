package com.vawndev.spring_boot_readnovel.Controllers;

import com.vawndev.spring_boot_readnovel.Dto.Requests.PageRequest;
import com.vawndev.spring_boot_readnovel.Dto.Responses.ApiResponse;
import com.vawndev.spring_boot_readnovel.Dto.Responses.Category.CategoriesResponse;
import com.vawndev.spring_boot_readnovel.Dto.Responses.PageResponse;
import com.vawndev.spring_boot_readnovel.Dto.Responses.Story.StoriesHomeResponse;
import com.vawndev.spring_boot_readnovel.Dto.Responses.Story.StoriesResponse;
import com.vawndev.spring_boot_readnovel.Services.CategoryService;
import com.vawndev.spring_boot_readnovel.Services.StoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class HomeController {
    private final CategoryService categoryService;
    private final StoryService storyService;

    @GetMapping("/homepage")
    public ApiResponse<StoriesHomeResponse> getStoriesHomepage(@RequestHeader(value = "Authorization" ,required = false) String BearerToken) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        PageRequest page1= new PageRequest(0,20);
        PageRequest page2 = new PageRequest(0,13);
        CategoriesResponse categories = categoryService.getCategories();
        PageResponse<StoriesResponse> STRS=storyService.getStories(page1);
        PageResponse<StoriesResponse> CMS=storyService.getStoriesComingSoon(page2);
        PageResponse<StoriesResponse> UDT=storyService.getStoriesUpdating(page2);
        PageResponse<StoriesResponse> RCM=storyService.recommendStories(page2,BearerToken);
        List<StoriesResponse> RSTR=storyService.getStoriesRank();
        StoriesHomeResponse storiesHomeResponse=StoriesHomeResponse
                .builder()
                .categories(categories)
                .stories(STRS)
                .getStoriesUpdating(UDT)
                .getStoriesComingSoon(CMS)
                .getStoriesRank(RSTR)
                .getStoriesRecommend(RCM)
                .build();
        return ApiResponse.<StoriesHomeResponse>builder().result(storiesHomeResponse).message("Successfully🎈").build();
    }
}
