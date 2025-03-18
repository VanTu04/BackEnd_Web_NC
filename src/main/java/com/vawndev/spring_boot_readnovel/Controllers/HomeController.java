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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class HomeController {
    private final CategoryService categoryService;
    private final StoryService storyService;

    @GetMapping("/homepage")
    public ApiResponse<StoriesHomeResponse> getStoriesHomepage() {
        PageRequest page1= new PageRequest(0,20);
        PageRequest page2 = new PageRequest(0,13);
        CategoriesResponse categories = categoryService.getCategories();
        PageResponse<StoriesResponse> STRS=storyService.getStories(page1);
        PageResponse<StoriesResponse> CMS=storyService.getStoriesComingSoon(page2);
        PageResponse<StoriesResponse> UDT=storyService.getStoriesUpdating(page2);
        List<StoriesResponse> RSTR=storyService.getStoriesRank();
        StoriesHomeResponse storiesHomeResponse=StoriesHomeResponse
                .builder()
                .categories(categories)
                .stories(STRS)
                .getStoriesUpdating(UDT)
                .getStoriesComingSoon(CMS)
                .getStoriesRank(RSTR)
                .build();
        return ApiResponse.<StoriesHomeResponse>builder().result(storiesHomeResponse).message("Successfully🎈").build();
    }
}
