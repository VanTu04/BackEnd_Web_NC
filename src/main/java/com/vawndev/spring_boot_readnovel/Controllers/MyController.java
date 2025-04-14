package com.vawndev.spring_boot_readnovel.Controllers;

import com.vawndev.spring_boot_readnovel.Dto.Requests.PageRequest;
import com.vawndev.spring_boot_readnovel.Dto.Requests.Story.StoryRequests;
import com.vawndev.spring_boot_readnovel.Dto.Responses.ApiResponse;
import com.vawndev.spring_boot_readnovel.Dto.Responses.Chapter.ChaptersResponse;
import com.vawndev.spring_boot_readnovel.Dto.Responses.My.ReadingHistoryResponse;
import com.vawndev.spring_boot_readnovel.Dto.Responses.PageResponse;
import com.vawndev.spring_boot_readnovel.Dto.Responses.Story.StoriesResponse;
import com.vawndev.spring_boot_readnovel.Dto.Responses.Subscription.SubscriptionResponse;
import com.vawndev.spring_boot_readnovel.Repositories.StoryRepository;
import com.vawndev.spring_boot_readnovel.Services.HistoryReadingService;
import com.vawndev.spring_boot_readnovel.Services.StoryService;
import com.vawndev.spring_boot_readnovel.Services.SubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/my")
@RequiredArgsConstructor
public class MyController {
    private final HistoryReadingService historyReadingService;
    private final StoryService storyService;
    private final SubscriptionService subscriptionService;

    @GetMapping("/list")
    public ApiResponse<PageResponse<StoriesResponse>> getList(@ModelAttribute PageRequest pageRequest) {
        PageResponse<StoriesResponse> result=storyService.getMyList(pageRequest);
        return ApiResponse.<PageResponse<StoriesResponse>>builder()
                .result(result)
                .message("Successfully")
                .build();
    }

    @GetMapping("/history")
    public ApiResponse<PageResponse<ReadingHistoryResponse>> getReadingHistory( @ModelAttribute  PageRequest pageRequest) {
        PageResponse<ReadingHistoryResponse> result=historyReadingService.getHistory(pageRequest);
        return ApiResponse.<PageResponse<ReadingHistoryResponse>>builder()
                .result(result)
                .message("Successfully")
                .build();
    }

    @GetMapping("/continue-reading")
    public ApiResponse<ChaptersResponse> getLatestChapter(@RequestParam String story_id) {
        ChaptersResponse chapter=historyReadingService.getLatestChapter(story_id);
        return ApiResponse.<ChaptersResponse>builder()
                .result(chapter)
                .message("Successfully")
                .build();
    }

    @DeleteMapping( "/history/delete")
    public ApiResponse<String> deleteReadingHistory(@RequestBody String story_id)  {
        historyReadingService.deleteHistory(story_id);
        return ApiResponse.<String>builder().result("Success!").build();
    }
    @DeleteMapping( "/history/deleteAll")
    public ApiResponse<String> deleteAllReadingHistory(@RequestHeader("Authorization") String authHeader)  {
        historyReadingService.deleteAllHistory();
        return ApiResponse.<String>builder().result("Success!").build();
    }

    @GetMapping("/subscription")
    public ApiResponse<SubscriptionResponse> getSubscriptions() {
        SubscriptionResponse response=subscriptionService.getSubscription();
        return ApiResponse.<SubscriptionResponse>builder().message("successfully").result(response).build();
    }

//    @PostMapping("/money/withdraw")
//    public ApiResponse<String> moneyWithdraw(@RequestHeader("Authorization") String authHeader,@RequestBody BigDecimal money)  {
//        historyReadingService.moneyWithdraw(authHeader);
//        return ApiResponse.<String>builder().result("Success!").build();
//    }
}
