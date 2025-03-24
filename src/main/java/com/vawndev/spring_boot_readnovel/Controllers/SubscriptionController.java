package com.vawndev.spring_boot_readnovel.Controllers;

import com.vawndev.spring_boot_readnovel.Dto.Requests.Subscription.SubscriptionRequest;
import com.vawndev.spring_boot_readnovel.Dto.Responses.ApiResponse;
import com.vawndev.spring_boot_readnovel.Dto.Responses.Subscription.SubscriptionPlansResponse;
import com.vawndev.spring_boot_readnovel.Dto.Responses.Subscription.SubscriptionResponse;
import com.vawndev.spring_boot_readnovel.Services.SubscriptionPlansService;
import com.vawndev.spring_boot_readnovel.Services.SubscriptionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/subscription")
@RequiredArgsConstructor
public class SubscriptionController {
    private final SubscriptionService subscriptionService;
    private final SubscriptionPlansService subscriptionPlansService;

    @PostMapping("/upgrade/role")
    public ApiResponse<String> upgradeRole(@RequestHeader("Authorization") String authHeader) {
        subscriptionService.upgradeRole(authHeader);
        return ApiResponse.<String>builder().message("Subscription role upgraded").build();
    }

    @PostMapping("/upgrade")
    public ApiResponse<String> upgradeSubscription(@RequestBody @Valid SubscriptionRequest req, @RequestHeader("Authorization") String authHeader) {
        subscriptionService.upgradeSubscription(req,authHeader);
        return ApiResponse.<String>builder().message("Subscription upgraded").build();
    }



    @GetMapping("")
    public ApiResponse<List<SubscriptionPlansResponse>> getSubscriptions() {
        List<SubscriptionPlansResponse> plans=subscriptionPlansService.getAllSubscriptionPlans();
        return ApiResponse.<List<SubscriptionPlansResponse>>builder().message("successfully").result(plans).build();
    }




}
