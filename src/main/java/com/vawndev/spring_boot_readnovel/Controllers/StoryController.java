package com.vawndev.spring_boot_readnovel.Controllers;


import com.vawndev.spring_boot_readnovel.Dto.Requests.PageRequest;
import com.vawndev.spring_boot_readnovel.Dto.Requests.Story.ModeratedByAdmin;
import com.vawndev.spring_boot_readnovel.Dto.Requests.Story.StoryRequests;
import com.vawndev.spring_boot_readnovel.Dto.Responses.ApiResponse;
import com.vawndev.spring_boot_readnovel.Dto.Responses.PageResponse;
import com.vawndev.spring_boot_readnovel.Dto.Responses.Story.StoriesResponse;
import com.vawndev.spring_boot_readnovel.Dto.Responses.Story.StoryDetailResponses;
import com.vawndev.spring_boot_readnovel.Services.StoryService;
import jakarta.validation.constraints.Future;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/story")
@RequiredArgsConstructor
public class StoryController {
    private final StoryService storyService;

    @GetMapping("/")
    public ApiResponse<PageResponse<StoriesResponse>> getStory(
            @RequestParam PageRequest req) {
        PageResponse<StoriesResponse> result=storyService.getStories(req);
        return ApiResponse.<PageResponse<StoriesResponse>>builder().result(result).build();
    }
    @GetMapping("/detail")
    public ApiResponse<StoryDetailResponses> getStoryDetail(@PathVariable String id) {
        StoryDetailResponses  result=storyService.getStoryById(id);
        return ApiResponse.<StoryDetailResponses>builder().result(result).build();
    }
    @PostMapping("/create")
    public ApiResponse<String> createStory(@RequestBody StoryRequests req) {
         storyService.addStory(req);
         return ApiResponse.<String>builder().result("Success!").build();
    }
    @PutMapping("/update")
    public ApiResponse<String> updateStory(@RequestBody StoryRequests req) {
        storyService.updateStoryByAuthor(req);
        return ApiResponse.<String>builder().result("Success!").build();
    }
    @PutMapping("/remove")
    public ApiResponse<String> removeStory(@RequestBody StoryRequests req) {
        storyService.deleteSoftStory(req.getEmailAuthor(),req.getId());
        return ApiResponse.<String>builder().result("success").build();
    }
    @DeleteMapping("/delete")
    public ApiResponse<String> deleteStory(@RequestBody ModeratedByAdmin req) {
        storyService.deleteStory(req);
        return ApiResponse.<String>builder().result("Sucess").build();
    }
}
