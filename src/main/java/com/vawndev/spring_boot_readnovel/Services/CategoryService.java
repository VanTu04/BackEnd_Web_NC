package com.vawndev.spring_boot_readnovel.Services;

import com.vawndev.spring_boot_readnovel.Dto.Requests.Category.CategoryRequests;
import com.vawndev.spring_boot_readnovel.Dto.Responses.Category.CategoriesResponse;

import java.util.List;

public interface CategoryService {
    CategoriesResponse getCategories();
    void addCategory(String name);
    void RemoveCategory(String name);
    void DeleteCategory(String name);
}
