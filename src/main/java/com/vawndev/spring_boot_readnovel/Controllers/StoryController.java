package com.vawndev.spring_boot_readnovel.Controllers;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.vawndev.spring_boot_readnovel.Dto.Requests.PageRequest;
import com.vawndev.spring_boot_readnovel.Dto.Requests.Story.ModeratedByAdmin;
import com.vawndev.spring_boot_readnovel.Dto.Requests.Story.StoryCondition;
import com.vawndev.spring_boot_readnovel.Dto.Requests.Story.StoryRequests;
import com.vawndev.spring_boot_readnovel.Dto.Responses.ApiResponse;
import com.vawndev.spring_boot_readnovel.Dto.Responses.PageResponse;
import com.vawndev.spring_boot_readnovel.Dto.Responses.Story.StoriesHomeResponse;
import com.vawndev.spring_boot_readnovel.Dto.Responses.Story.StoriesResponse;
import com.vawndev.spring_boot_readnovel.Dto.Responses.Story.StoryDetailResponses;
import com.vawndev.spring_boot_readnovel.Services.StoryService;
import com.vawndev.spring_boot_readnovel.Utils.Help.JsonHelper;
import com.vawndev.spring_boot_readnovel.Utils.PaginationUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/story")
@RequiredArgsConstructor
public class StoryController {
    private final StoryService storyService;

    @GetMapping("")
    public ApiResponse<PageResponse<StoriesResponse>> getStory(
            @ModelAttribute  PageRequest req) {
        PageResponse<StoriesResponse> result=storyService.getStories(req);
        return ApiResponse.<PageResponse<StoriesResponse>>builder().result(result).build();
    }

    @GetMapping("/detail/{id}")
    public ApiResponse<StoryDetailResponses> getStoryDetail(@PathVariable String id) {
        StoryDetailResponses result=storyService.getStoryById(id);
        return ApiResponse.<StoryDetailResponses>builder().result(result).build();
    }
    @PostMapping(value = "/create", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public ApiResponse<String> createStory(@RequestHeader("Authorization") String authHeader,@RequestPart String storyJson, @RequestPart MultipartFile image_cover)  {
            StoryRequests storyRequests= JsonHelper.parseJson(storyJson, StoryRequests.class);
            storyService.addStory(storyRequests,image_cover,authHeader);
            return ApiResponse.<String>builder().result("Success!").build();
    }

    @PatchMapping("/update")
    public ApiResponse<String> updateStory(@RequestHeader("Authorization") String authHeader,@RequestParam StoryRequests req,@RequestParam String id) {
        storyService.updateStoryByAuthor(req,id,authHeader);
        return ApiResponse.<String>builder().result("Success!").build();
    }
    @PutMapping(value = "/update/cover", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ApiResponse<String> updateStoryCover(
            @RequestParam StoryCondition req,
            @RequestPart MultipartFile image_cover,
            @RequestHeader("Authorization") String authHeader) {

        storyService.updateCoverImage(req, image_cover,authHeader);
        return ApiResponse.<String>builder().result("success").build();
    }

    @PutMapping("/remove")
    public ApiResponse<String> removeStory(@RequestBody StoryCondition req ,@RequestHeader("Authorization") String authHeader) {
        storyService.deleteSoftStory(req,authHeader);
        return ApiResponse.<String>builder().result("success").build();
    }

    @DeleteMapping("/delete")
    public ApiResponse<String> deleteStory(@RequestBody StoryCondition req,@RequestHeader("Authorization") String authHeader) {
        storyService.deleteStory(req,authHeader);
        return ApiResponse.<String>builder().result("Success").build();
    }
    
}
