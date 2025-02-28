package com.vawndev.spring_boot_readnovel.Controllers;

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
    public ApiResponse<CategoriesResponse> addCategory(@RequestBody String name){
        categoryService.addCategory(name);
        return ApiResponse.<CategoriesResponse>builder().message("Successfully!").build();
    }
}
