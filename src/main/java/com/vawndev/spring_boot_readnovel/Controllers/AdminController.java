package com.vawndev.spring_boot_readnovel.Controllers;

import com.vawndev.spring_boot_readnovel.Dto.Requests.Category.CategoryRequests;
import com.vawndev.spring_boot_readnovel.Dto.Requests.PageRequest;
import com.vawndev.spring_boot_readnovel.Dto.Requests.Story.ModeratedByAdmin;
import com.vawndev.spring_boot_readnovel.Dto.Responses.ApiResponse;
import com.vawndev.spring_boot_readnovel.Dto.Responses.Category.CategoriesResponse;
import com.vawndev.spring_boot_readnovel.Dto.Responses.PageResponse;
import com.vawndev.spring_boot_readnovel.Dto.Responses.User.UserDetailReponse;
import com.vawndev.spring_boot_readnovel.Dto.Responses.User.UserResponse;
import com.vawndev.spring_boot_readnovel.Services.CategoryService;
import com.vawndev.spring_boot_readnovel.Services.StoryService;
import com.vawndev.spring_boot_readnovel.Services.UserService;
import com.vawndev.spring_boot_readnovel.Utils.Help.JsonHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {
    private final StoryService storyService;
    private final CategoryService categoryService;
    private final UserService userService;

    @GetMapping("/category")
    public ApiResponse<CategoriesResponse> getCategories(){
        CategoriesResponse res=categoryService.getCategories();
        return ApiResponse.<CategoriesResponse>builder().result(res).message("Successfully!").build();
    }

    @PostMapping("/moderated")
    public ApiResponse<String> ModeratedByAdmin (@RequestBody ModeratedByAdmin req) {
        storyService.ModeratedByAdmin(req);
        return ApiResponse.<String>builder().result("Success!").build();
    }

    @PostMapping("/category/add")
    public ApiResponse<String> addCategory(@RequestBody String name){
        categoryService.addCategory(JsonHelper.getValueFromJson(name,"name"));
        return ApiResponse.<String>builder().result("Successfully!").build();
    }

    @PutMapping("/category/update")
    public ApiResponse<String> updateCategory(@RequestBody CategoryRequests req, @RequestParam String id){
        categoryService.UpdateCategory(req,id);
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

    @GetMapping("/user")
    public ApiResponse<PageResponse<UserDetailReponse>> getUser(@ModelAttribute PageRequest req){
        PageResponse<UserDetailReponse> users=userService.getAllUser(req);
        return ApiResponse.<PageResponse<UserDetailReponse>>builder().result(users).build();
    }
}
