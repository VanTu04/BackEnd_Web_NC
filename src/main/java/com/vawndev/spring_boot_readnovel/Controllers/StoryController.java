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
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
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
    public ApiResponse<StoryDetailResponses> getStoryDetail(@RequestHeader(value = "Authorization",required = false)  String bearerToken,@PathVariable @NotBlank String id,@ModelAttribute  PageRequest req) {
        StoryDetailResponses result=storyService.getStoryById(bearerToken, id,req);
        return ApiResponse.<StoryDetailResponses>builder().result(result).build();
    }
    @PostMapping(value = "/create", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public ApiResponse<String> createStory(@RequestPart @NotBlank String storyJson, @RequestPart MultipartFile image_cover)  {
            StoryRequests storyRequests= JsonHelper.parseJson(storyJson, StoryRequests.class);
            storyService.addStory(storyRequests,image_cover);
            return ApiResponse.<String>builder().result("Success!").build();
    }

    @PatchMapping("/update")
    public ApiResponse<String> updateStory( @RequestParam @Valid StoryRequests req, @RequestParam @NotBlank String id) {
        storyService.updateStoryByAuthor(req,id);
        return ApiResponse.<String>builder().result("Success!").build();
    }
    @PutMapping(value = "/update/cover", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ApiResponse<String> updateStoryCover(
            @RequestParam StoryCondition req,
            @RequestPart MultipartFile image_cover
            ) {

        storyService.updateCoverImage(req, image_cover);
        return ApiResponse.<String>builder().result("success").build();
    }

    @PutMapping("/remove")
    public ApiResponse<String> removeStory(@RequestBody @Valid StoryCondition req ) {
        storyService.deleteSoftStory(req);
        return ApiResponse.<String>builder().result("success").build();
    }

    @DeleteMapping("/delete")
    public ApiResponse<String> deleteStory(@RequestBody @Valid StoryCondition req) {
        storyService.deleteStory(req);
        return ApiResponse.<String>builder().result("Success").build();
    }

    
}
