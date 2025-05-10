package com.vawndev.spring_boot_readnovel.Controllers;

import com.vawndev.spring_boot_readnovel.Dto.Requests.Category.CategoryRequests;
import com.vawndev.spring_boot_readnovel.Dto.Requests.ConditionRequest;
import com.vawndev.spring_boot_readnovel.Dto.Requests.PageRequest;
import com.vawndev.spring_boot_readnovel.Dto.Requests.Story.ModeratedByAdmin;
import com.vawndev.spring_boot_readnovel.Dto.Requests.Subscription.SubscriptionCreatePlansRequest;
import com.vawndev.spring_boot_readnovel.Dto.Requests.Subscription.SubscriptionPlansRequest;
import com.vawndev.spring_boot_readnovel.Dto.Responses.ApiResponse;
import com.vawndev.spring_boot_readnovel.Dto.Responses.Category.CategoriesResponse;
import com.vawndev.spring_boot_readnovel.Dto.Responses.Payment.WalletTransactionResponse;
import com.vawndev.spring_boot_readnovel.Dto.Responses.Story.StoriesResponse;
import com.vawndev.spring_boot_readnovel.Dto.Responses.PageResponse;
import com.vawndev.spring_boot_readnovel.Dto.Responses.Subscription.SubscriptionPlansResponse;
import com.vawndev.spring_boot_readnovel.Dto.Responses.User.UserDetailResponse;
import com.vawndev.spring_boot_readnovel.Dto.Responses.WithdrawResponse;
import com.vawndev.spring_boot_readnovel.Enum.TransactionStatus;
import com.vawndev.spring_boot_readnovel.Services.*;
import com.vawndev.spring_boot_readnovel.Utils.Help.JsonHelper;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import com.vawndev.spring_boot_readnovel.Dto.Responses.User.AdminUserDetailResponse;
import com.vawndev.spring_boot_readnovel.Entities.Category;
import com.vawndev.spring_boot_readnovel.Repositories.CategoryRepository;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {
    private final StoryService storyService;
    private final CategoryService categoryService;
    private final UserService userService;
    private final SubscriptionPlansService subscriptionPlansService;
    private final WithdrawService withdrawService;
    private final CategoryRepository categoryRepository;
    private final WalletTransactionService walletTransactionService;

    // =================== CATEGORY MANAGEMENT ===================
    @GetMapping("/category")
    public ApiResponse<CategoriesResponse> getCategories() {
        CategoriesResponse res = categoryService.getCategories();
        return ApiResponse.<CategoriesResponse>builder().result(res).message("Successfully!").build();
    }

    @PostMapping("/category/add")
    public ApiResponse<String> addCategory(@RequestBody @NotBlank String name) {
        categoryService.addCategory(JsonHelper.getValueFromJson(name, "name"));
        return ApiResponse.<String>builder().result("Successfully!").build();
    }

    @PutMapping("/category/update")
    public ApiResponse<String> updateCategory(@RequestBody @Valid CategoryRequests req) {
        categoryService.UpdateCategory(req);
        return ApiResponse.<String>builder().message("Successfully!").build();
    }

    @PostMapping("/category/remove")
    public ApiResponse<String> removeCategory(@RequestParam @NotBlank String id) {
        categoryService.RemoveCategory(id);
        return ApiResponse.<String>builder().message("Successfully!").build();
    }

    @PostMapping("/category/delete")
    public ApiResponse<String> deleteCategory(@RequestParam @NotBlank String id) {
        categoryService.DeleteCategory(id);
        return ApiResponse.<String>builder().message("Successfully!").build();
    }

    @GetMapping("/category/search")
    public ApiResponse<List<Category>> searchCategories(@RequestParam(required = false) String q) {
        List<Category> data;
        if (q != null && !q.isEmpty()) {
            data = categoryRepository.findByName(q)
                    .map(List::of)
                    .orElseGet(List::of);
        } else {
            data = categoryRepository.findAll();
        }
        return ApiResponse.<List<Category>>builder()
                .result(data)
                .message("Categories retrieved successfully!")
                .build();
    }

    // =================== STORY MANAGEMENT ===================
    @GetMapping("/story")
    public ApiResponse<PageResponse<StoriesResponse>> getStory(
            @ModelAttribute PageRequest req) {
        PageResponse<StoriesResponse> result = storyService.getStoriesByAdmin(req);
        return ApiResponse.<PageResponse<StoriesResponse>>builder().result(result).build();
    }

    @PostMapping("/story/moderated")
    public ApiResponse<String> ModeratedByAdmin(@RequestBody @Valid ModeratedByAdmin req) {
        storyService.ModeratedByAdmin(req);
        return ApiResponse.<String>builder().result("Success!").build();
    }

    @PutMapping("/story/{id}/ban-status")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ApiResponse<String> updateStoryBanStatus(
            @PathVariable String id,
            @RequestParam boolean isBan) {
        storyService.updateStoryBanStatus(id, isBan);
        return ApiResponse.<String>builder()
                .message(isBan ? "Story has been banned" : "Story has been unbanned")
                .build();
    }

    // =================== USER MANAGEMENT ===================
    @GetMapping("/user")
    public ApiResponse<PageResponse<AdminUserDetailResponse>> getUser(@ModelAttribute PageRequest req) {

        PageResponse<AdminUserDetailResponse> users = userService.getAllUser(req);
        return ApiResponse.<PageResponse<AdminUserDetailResponse>>builder().result(users).build();
    }

        @PutMapping("/user/{id}/deactivate")
        public ApiResponse<String> deactivateUser(@PathVariable String id) {
            userService.deactivateUser(id);
            return ApiResponse.<String>builder()
                    .message("User deactivated successfully")
                    .build();
        }
    // =================== SUBSCRIPTION PLANS MANAGEMENT ===================
    @GetMapping("/subscription")
    public ApiResponse<List<SubscriptionPlansResponse>> getAllSubscriptionPlans() {
        List<SubscriptionPlansResponse> plans = subscriptionPlansService.getAllSubscriptionPlans();
        return ApiResponse.<List<SubscriptionPlansResponse>>builder().result(plans).message("Successfully!").build();
    }

    @PostMapping("/subscription/add")
    public ApiResponse<String> createSubscriptionPlan(@RequestBody @Valid SubscriptionCreatePlansRequest req) {
        subscriptionPlansService.createSubscriptionPlan(req);
        return ApiResponse.<String>builder().result("Subscription plan created successfully!").build();
    }

    @PatchMapping("/subscription/update")
    public ApiResponse<String> updateSubscriptionPlan(@RequestBody @Valid SubscriptionPlansRequest req,
            @RequestParam @NotBlank String id_plan) {
        subscriptionPlansService.updateSubscriptionPlan(req, id_plan);
        return ApiResponse.<String>builder().result("Subscription plan updated successfully!").build();
    }

    @PutMapping("/subscription/remove")
    public ApiResponse<String> removeSubscriptionPlan(@RequestParam @NotBlank String id_plan) {
        subscriptionPlansService.removeSubscriptionPlan(id_plan);
        return ApiResponse.<String>builder().result("Subscription plan removed successfully!").build();
    }

    @PutMapping("/subscription/edit")
    public ApiResponse<String> editSubscriptionPlan(@RequestBody @Valid ConditionRequest req,
            @RequestParam @NotBlank String id_plan) {
        subscriptionPlansService.editSubscriptionPlan(req, id_plan);
        return ApiResponse.<String>builder().result("Subscription plan removed successfully!").build();
    }

    @GetMapping("/subscription/trash")
    public ApiResponse<List<SubscriptionPlansResponse>> getAllSubscriptionPlansTrash() {
        List<SubscriptionPlansResponse> subscriptionPlansResponses = subscriptionPlansService
                .getAllSubscriptionPlansTrash();
        return ApiResponse.<List<SubscriptionPlansResponse>>builder().result(subscriptionPlansResponses).build();
    }

    @DeleteMapping("/subscription/delete")
    public ApiResponse<String> deleteSubscriptionPlan(@RequestParam @NotBlank String id_plan) {
        subscriptionPlansService.deleteSubscriptionPlan(id_plan);
        return ApiResponse.<String>builder().result("Subscription plan deleted successfully!").build();
    }
    // =================== WITHDRAW MANAGEMENT ===================

    @PutMapping("/withdraw/{id}")
    public ApiResponse<WithdrawResponse> approveWithdrawByAdmin(
            @RequestHeader("Authorization") String bearerToken,
            @PathVariable("id") String withdrawId,
            @RequestParam TransactionStatus status) {
        WithdrawResponse response = withdrawService.approvedByAdmin(withdrawId, status);
        return ApiResponse.<WithdrawResponse>builder()
                .result(response)
                .message("Successfully!")
                .build();
    }

    // =================== TRANSACTION MANAGEMENT ===================
    @GetMapping("/wallet")
    public ApiResponse<?> findAll(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "5") int size) {
        return ApiResponse.<PageResponse<WalletTransactionResponse>>builder()
                .message("Successfully")
                .result(walletTransactionService.getAllWalletTransactions(page, size))
                .build();
    }

    @GetMapping("/wallet/search")
    public ApiResponse<?> findAllById(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "5") int size) {
        return ApiResponse.<PageResponse<WalletTransactionResponse>>builder()
                .message("Successfully")
                .result(walletTransactionService.getWalletTransactionsByUserId(page, size))
                .build();
    }

}
