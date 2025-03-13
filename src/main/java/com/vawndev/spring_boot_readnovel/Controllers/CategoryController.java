package com.vawndev.spring_boot_readnovel.Controllers;

import com.vawndev.spring_boot_readnovel.Dto.Requests.Category.CategoryRequests;
import com.vawndev.spring_boot_readnovel.Dto.Responses.ApiResponse;
import com.vawndev.spring_boot_readnovel.Dto.Responses.Category.CategoriesResponse;
import com.vawndev.spring_boot_readnovel.Services.CategoryService;
import com.vawndev.spring_boot_readnovel.Utils.Help.JsonHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/category")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;

    @GetMapping("")
    public ApiResponse<CategoriesResponse> getCategories(){
        CategoriesResponse res=categoryService.getCategories();
        return ApiResponse.<CategoriesResponse>builder().result(res).message("Successfully!").build();
    }


}
