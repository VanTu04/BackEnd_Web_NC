package com.vawndev.spring_boot_readnovel.Controllers;

import com.vawndev.spring_boot_readnovel.Dto.Requests.Category.CategoryRequests;
import com.vawndev.spring_boot_readnovel.Dto.Responses.ApiResponse;
import com.vawndev.spring_boot_readnovel.Dto.Responses.Category.CategoriesResponse;
import com.vawndev.spring_boot_readnovel.Services.CategoryService;
import com.vawndev.spring_boot_readnovel.Util.Help.JsonHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/Category")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;

    @GetMapping("")
    public ApiResponse<CategoriesResponse> getCategories(){
        CategoriesResponse res=categoryService.getCategories();
        return ApiResponse.<CategoriesResponse>builder().result(res).message("Successfully!").build();
    }

    @PostMapping("/add")
    public ApiResponse<String> addCategory(@RequestBody String name){
        categoryService.addCategory(JsonHelper.getValueFromJson(name,"name"));
        return ApiResponse.<String>builder().result("Successfully!").build();
    }

    @PutMapping("/update")
    public ApiResponse<String> updateCategory(@RequestBody CategoryRequests req,@RequestParam String id){
        categoryService.UpdateCategory(req,id);
        return ApiResponse.<String>builder().message("Successfully!").build();
    }

    @PostMapping("/remove")
    public ApiResponse<String> removeCategory(@RequestParam String id){
        categoryService.RemoveCategory(id);
        return ApiResponse.<String>builder().message("Successfully!").build();
    }
    @PostMapping("/delete")
    public ApiResponse<String> deleteCategory(@RequestParam String id){
        categoryService.DeleteCategory(id);
        return ApiResponse.<String>builder().message("Successfully!").build();
    }
}
