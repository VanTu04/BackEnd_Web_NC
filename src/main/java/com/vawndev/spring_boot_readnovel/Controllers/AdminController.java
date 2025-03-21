package com.vawndev.spring_boot_readnovel.Controllers;

import com.vawndev.spring_boot_readnovel.Dto.Requests.Category.CategoryRequests;
import com.vawndev.spring_boot_readnovel.Dto.Requests.ConditionRequest;
import com.vawndev.spring_boot_readnovel.Dto.Requests.PageRequest;
import com.vawndev.spring_boot_readnovel.Dto.Requests.Story.ModeratedByAdmin;
import com.vawndev.spring_boot_readnovel.Dto.Requests.Subscription.SubscriptionCreatePlansRequest;
import com.vawndev.spring_boot_readnovel.Dto.Requests.Subscription.SubscriptionPlansRequest;
import com.vawndev.spring_boot_readnovel.Dto.Responses.ApiResponse;
import com.vawndev.spring_boot_readnovel.Dto.Responses.Category.CategoriesResponse;
import com.vawndev.spring_boot_readnovel.Dto.Responses.PageResponse;
import com.vawndev.spring_boot_readnovel.Dto.Responses.Subscription.SubscriptionPlansResponse;
import com.vawndev.spring_boot_readnovel.Dto.Responses.User.UserDetailReponse;
import com.vawndev.spring_boot_readnovel.Services.CategoryService;
import com.vawndev.spring_boot_readnovel.Services.StoryService;
import com.vawndev.spring_boot_readnovel.Services.SubscriptionPlansService;
import com.vawndev.spring_boot_readnovel.Services.UserService;
import com.vawndev.spring_boot_readnovel.Utils.Help.JsonHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {
    private final StoryService storyService;
    private final CategoryService categoryService;
    private final UserService userService;
    private final SubscriptionPlansService subscriptionPlansService;

    // =================== CATEGORY MANAGEMENT ===================
    @GetMapping("/category")
    public ApiResponse<CategoriesResponse> getCategories(){
        CategoriesResponse res = categoryService.getCategories();
        return ApiResponse.<CategoriesResponse>builder().result(res).message("Successfully!").build();
    }

    @PostMapping("/category/add")
    public ApiResponse<String> addCategory(@RequestBody String name){
        categoryService.addCategory(JsonHelper.getValueFromJson(name, "name"));
        return ApiResponse.<String>builder().result("Successfully!").build();
    }

    @PutMapping("/category/update")
    public ApiResponse<String> updateCategory(@RequestBody CategoryRequests req, @RequestParam String id){
        categoryService.UpdateCategory(req, id);
        return ApiResponse.<String>builder().message("Successfully!").build();
    }

    @PostMapping("/category/remove")
    public ApiResponse<String> removeCategory(@RequestParam String id){
        categoryService.RemoveCategory(id);
        return ApiResponse.<String>builder().message("Successfully!").build();
    }

    @PostMapping("/category/delete")
    public ApiResponse<String> deleteCategory(@RequestParam String id){
        categoryService.DeleteCategory(id);
        return ApiResponse.<String>builder().message("Successfully!").build();
    }

    // =================== STORY MODERATION ===================
    @PostMapping("/moderated")
    public ApiResponse<String> ModeratedByAdmin(@RequestBody ModeratedByAdmin req) {
        storyService.ModeratedByAdmin(req);
        return ApiResponse.<String>builder().result("Success!").build();
    }

    // =================== USER MANAGEMENT ===================
    @GetMapping("/user")
    public ApiResponse<PageResponse<UserDetailReponse>> getUser(@ModelAttribute PageRequest req){
        PageResponse<UserDetailReponse> users = userService.getAllUser(req);
        return ApiResponse.<PageResponse<UserDetailReponse>>builder().result(users).build();
    }

    // =================== SUBSCRIPTION PLANS MANAGEMENT ===================
    @GetMapping("/subscription")
    public ApiResponse<List<SubscriptionPlansResponse>> getAllSubscriptionPlans(){
        List<SubscriptionPlansResponse> plans = subscriptionPlansService.getAllSubscriptionPlans();
        return ApiResponse.<List<SubscriptionPlansResponse>>builder().result(plans).message("Successfully!").build();
    }

    @PostMapping("/subscription/add")
    public ApiResponse<String> createSubscriptionPlan(@RequestBody SubscriptionCreatePlansRequest req, @RequestHeader("Authorization") String bearerToken){
        subscriptionPlansService.createSubscriptionPlan(req, bearerToken);
        return ApiResponse.<String>builder().result("Subscription plan created successfully!").build();
    }

    @PatchMapping("/subscription/update")
    public ApiResponse<String> updateSubscriptionPlan(@RequestBody SubscriptionPlansRequest req, @RequestHeader("Authorization") String bearerToken){
        subscriptionPlansService.updateSubscriptionPlan(req, bearerToken);
        return ApiResponse.<String>builder().result("Subscription plan updated successfully!").build();
    }

    @PutMapping("/subscription/remove")
    public ApiResponse<String> removeSubscriptionPlan(@RequestBody ConditionRequest req, @RequestHeader("Authorization") String bearerToken){
        subscriptionPlansService.removeSubscriptionPlan(req, bearerToken);
        return ApiResponse.<String>builder().result("Subscription plan removed successfully!").build();
    }
    @PutMapping("/subscription/edit")
    public ApiResponse<String> editSubscriptionPlan(@RequestBody ConditionRequest req, @RequestHeader("Authorization") String bearerToken){
        subscriptionPlansService.editSubscriptionPlan(req, bearerToken);
        return ApiResponse.<String>builder().result("Subscription plan removed successfully!").build();
    }
    @GetMapping("/subscription/trash")
    public ApiResponse<List<SubscriptionPlansResponse>> getAllSubscriptionPlansTrash(@RequestParam String email, @RequestHeader("Authorization") String bearerToken){
        List<SubscriptionPlansResponse> subscriptionPlansResponses= subscriptionPlansService.getAllSubscriptionPlansTrash(email,bearerToken);
        return ApiResponse.<List<SubscriptionPlansResponse>>builder().result(subscriptionPlansResponses).build();
    }
    @DeleteMapping("/subscription/delete")
    public ApiResponse<String> deleteSubscriptionPlan(@RequestBody ConditionRequest req, @RequestHeader("Authorization") String bearerToken){
        subscriptionPlansService.deleteSubscriptionPlan(req, bearerToken);
        return ApiResponse.<String>builder().result("Subscription plan deleted successfully!").build();
    }
}
