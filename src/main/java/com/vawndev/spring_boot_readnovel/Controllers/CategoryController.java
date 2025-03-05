package com.vawndev.spring_boot_readnovel.Controllers;

import com.vawndev.spring_boot_readnovel.Dto.Requests.Category.CategoryRequests;
import com.vawndev.spring_boot_readnovel.Dto.Responses.ApiResponse;
import com.vawndev.spring_boot_readnovel.Dto.Responses.Category.CategoriesResponse;
import com.vawndev.spring_boot_readnovel.Services.CategoryService;
import lombok.RequiredArgsConstructor;
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
    public ApiResponse<String> addCategory(@RequestBody String id){
        categoryService.addCategory(id);
        return ApiResponse.<String>builder().message("Successfully!").build();
    }

    @PutMapping("/update")
    public ApiResponse<String> updateCategory(@RequestBody CategoryRequests req){
        categoryService.UpdateCategory(req);
        return ApiResponse.<String>builder().message("Successfully!").build();
    }

    @PostMapping("/remove")
    public ApiResponse<String> removeCategory(@RequestBody String id){
        categoryService.RemoveCategory(id);
        return ApiResponse.<String>builder().message("Successfully!").build();
    }
    @PostMapping("/delete")
    public ApiResponse<String> deleteCategory(@RequestBody String id){
        categoryService.DeleteCategory(id);
        return ApiResponse.<String>builder().message("Successfully!").build();
    }
}
