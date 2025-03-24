package com.vawndev.spring_boot_readnovel.Controllers;

import com.vawndev.spring_boot_readnovel.Dto.Requests.PageRequest;
import com.vawndev.spring_boot_readnovel.Dto.Requests.Story.StoryRequests;
import com.vawndev.spring_boot_readnovel.Dto.Responses.ApiResponse;
import com.vawndev.spring_boot_readnovel.Dto.Responses.My.ReadingHistoryResponse;
import com.vawndev.spring_boot_readnovel.Dto.Responses.PageResponse;
import com.vawndev.spring_boot_readnovel.Dto.Responses.Subscription.SubscriptionResponse;
import com.vawndev.spring_boot_readnovel.Services.HistoryReadingService;
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
    private final SubscriptionService subscriptionService;

    @GetMapping("/history")
    public ApiResponse<PageResponse<ReadingHistoryResponse>> getReadingHistory(@RequestHeader("Authorization")String authHeader, @RequestParam PageRequest pageRequest) {
        PageResponse<ReadingHistoryResponse> result=historyReadingService.getHistory(authHeader, pageRequest);
        return ApiResponse.<PageResponse<ReadingHistoryResponse>>builder()
                .result(result)
                .message("Successfully")
                .build();
    }

    @DeleteMapping( "/history/delete")
    public ApiResponse<String> deleteReadingHistory(@RequestHeader("Authorization") String authHeader,@RequestBody String story_id)  {
        historyReadingService.deleteHistory(authHeader,story_id);
        return ApiResponse.<String>builder().result("Success!").build();
    }
    @DeleteMapping( "/history/deleteAll")
    public ApiResponse<String> deleteAllReadingHistory(@RequestHeader("Authorization") String authHeader)  {
        historyReadingService.deleteAllHistory(authHeader);
        return ApiResponse.<String>builder().result("Success!").build();
    }

    @GetMapping("/subscription")
    public ApiResponse<SubscriptionResponse> getSubscriptions(@RequestHeader("Authorization") String authHeader, @RequestParam String email) {
        SubscriptionResponse response=subscriptionService.getSubscription(email,authHeader);
        return ApiResponse.<SubscriptionResponse>builder().message("successfully").result(response).build();
    }

//    @PostMapping("/money/withdraw")
//    public ApiResponse<String> moneyWithdraw(@RequestHeader("Authorization") String authHeader,@RequestBody BigDecimal money)  {
//        historyReadingService.moneyWithdraw(authHeader);
//        return ApiResponse.<String>builder().result("Success!").build();
//    }
}
