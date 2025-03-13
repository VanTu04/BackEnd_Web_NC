package com.vawndev.spring_boot_readnovel.Controllers;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.vawndev.spring_boot_readnovel.Dto.Requests.PageRequest;
import com.vawndev.spring_boot_readnovel.Dto.Requests.Story.ModeratedByAdmin;
import com.vawndev.spring_boot_readnovel.Dto.Requests.Story.StoryRequests;
import com.vawndev.spring_boot_readnovel.Dto.Responses.ApiResponse;
import com.vawndev.spring_boot_readnovel.Dto.Responses.PageResponse;
import com.vawndev.spring_boot_readnovel.Dto.Responses.Story.StoriesResponse;
import com.vawndev.spring_boot_readnovel.Dto.Responses.Story.StoryDetailResponses;
import com.vawndev.spring_boot_readnovel.Enum.StoryType;
import com.vawndev.spring_boot_readnovel.Services.StoryService;
import com.vawndev.spring_boot_readnovel.Utils.Help.JsonHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequestMapping("/story")
@RequiredArgsConstructor
public class StoryController {
    private final StoryService storyService;

    @GetMapping("")
    public ApiResponse<PageResponse<StoriesResponse>> getStory(
            @ModelAttribute PageRequest req) {
        PageResponse<StoriesResponse> result=storyService.getStories(req);
        return ApiResponse.<PageResponse<StoriesResponse>>builder().result(result).build();
    }
    @GetMapping("/detail/{id}")
    public ApiResponse<StoryDetailResponses> getStoryDetail(@PathVariable String id) {
        StoryDetailResponses result=storyService.getStoryById(id);
        return ApiResponse.<StoryDetailResponses>builder().result(result).build();
    }
    @PostMapping(value = "/create", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public ApiResponse<String> createStory(@RequestPart String storyJson, @RequestPart MultipartFile image_cover) throws JsonProcessingException {
            StoryRequests storyRequests= JsonHelper.parseJson(storyJson, StoryRequests.class);
            storyService.addStory(storyRequests,image_cover);
            return ApiResponse.<String>builder().result("Success!").build();
    }

    @PutMapping("/update")
    public ApiResponse<String> updateStory(@RequestBody StoryRequests req,@RequestParam String id,@RequestPart MultipartFile images) {
        storyService.updateStoryByAuthor(req,id,images);
        return ApiResponse.<String>builder().result("Success!").build();
    }
    @PutMapping("/remove")
    public ApiResponse<String> removeStory(@RequestBody StoryRequests req,@RequestParam String id) {
        storyService.deleteSoftStory(req.getEmailAuthor(),id);
        return ApiResponse.<String>builder().result("success").build();
    }

    @DeleteMapping("/delete")
    public ApiResponse<String> deleteStory(@RequestBody ModeratedByAdmin req) {
        storyService.deleteStory(req);
        return ApiResponse.<String>builder().result("Sucess").build();
    }
    
}
