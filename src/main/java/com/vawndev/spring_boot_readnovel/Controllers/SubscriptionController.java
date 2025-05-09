package com.vawndev.spring_boot_readnovel.Controllers;

import com.vawndev.spring_boot_readnovel.Dto.Requests.Subscription.SubscriptionRequest;
import com.vawndev.spring_boot_readnovel.Dto.Responses.ApiResponse;
import com.vawndev.spring_boot_readnovel.Dto.Responses.Subscription.PlansResponse;
import com.vawndev.spring_boot_readnovel.Dto.Responses.Subscription.RolePlanRespone;
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
    public ApiResponse<String> upgradeRole() {
        subscriptionService.upgradeRole();
        return ApiResponse.<String>builder().message("Subscription role upgraded").build();
    }

    @PostMapping("/upgrade")
    public ApiResponse<String> upgradeSubscription(@RequestBody @Valid SubscriptionRequest req) {
        subscriptionService.upgradeSubscription(req);
        return ApiResponse.<String>builder().message("Subscription upgraded").build();
    }

    @GetMapping("")
    public ApiResponse<PlansResponse> getSubscriptions() {
        List<SubscriptionPlansResponse> subscription = subscriptionPlansService.getAllSubscriptionPlans();
        RolePlanRespone role = subscriptionPlansService.getRoleToUpgrade();
        PlansResponse plan = PlansResponse.builder().author_role(role).plans(subscription).build();
        return ApiResponse.<PlansResponse>builder().message("successfully").result(plan).build();
    }

}
