package com.vawndev.spring_boot_readnovel.Services;

import com.vawndev.spring_boot_readnovel.Dto.Responses.Category.CategoriesResponse;

import java.util.List;

public interface CategoryService {
    List<CategoriesResponse> getCategories();

}
