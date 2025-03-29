package com.vawndev.spring_boot_readnovel.Controllers;


import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.vawndev.spring_boot_readnovel.Dto.Requests.PageRequest;
import com.vawndev.spring_boot_readnovel.Dto.Requests.Story.StoryCondition;
import com.vawndev.spring_boot_readnovel.Dto.Requests.Story.StoryRequests;
import com.vawndev.spring_boot_readnovel.Dto.Responses.ApiResponse;
import com.vawndev.spring_boot_readnovel.Dto.Responses.PageResponse;
import com.vawndev.spring_boot_readnovel.Dto.Responses.Story.StoriesResponse;
import com.vawndev.spring_boot_readnovel.Dto.Responses.Story.StoryDetailResponses;
import com.vawndev.spring_boot_readnovel.Entities.ReadingHistory;
import com.vawndev.spring_boot_readnovel.Services.ReadingHistoryService;
import com.vawndev.spring_boot_readnovel.Services.StoryService;
import com.vawndev.spring_boot_readnovel.Utils.Help.JsonHelper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/story")
@RequiredArgsConstructor
public class StoryController {
    private final StoryService storyService;
    private final ReadingHistoryService readingHistoryService;

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

    @PostMapping("/read")
    public ApiResponse<String> saveReadingHistory(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam String storyId,
            @RequestParam String chapterId) {
        String userId = getUserIdFromAuthHeader(authHeader); // Lấy userId từ Authorization header
        readingHistoryService.saveReadingHistory(userId, storyId, chapterId);
        return ApiResponse.<String>builder().result("Reading history saved successfully").build();
    }

    @GetMapping("/reading-history")
    public ApiResponse<List<ReadingHistory>> getReadingHistory(
            @RequestHeader("Authorization") String authHeader) {
        String userId = getUserIdFromAuthHeader(authHeader); // Lấy userId từ Authorization header
        List<ReadingHistory> history = readingHistoryService.getReadingHistory(userId);
        return ApiResponse.<List<ReadingHistory>>builder().result(history).build();
    }

    private String getUserIdFromAuthHeader(String authHeader) {
        // Giả sử bạn có logic để giải mã Authorization header và lấy userId
        // Ví dụ: authHeader chứa JWT token, bạn cần giải mã để lấy userId
        // Thay thế đoạn này bằng logic thực tế của bạn
        return "example-user-id"; // Đây chỉ là giá trị ví dụ, thay bằng logic thực tế
    }
    
}
