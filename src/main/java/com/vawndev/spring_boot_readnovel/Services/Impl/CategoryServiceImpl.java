package com.vawndev.spring_boot_readnovel.Services.Impl;

import com.vawndev.spring_boot_readnovel.Dto.Requests.Category.CategoryRequests;
import com.vawndev.spring_boot_readnovel.Dto.Responses.Category.CategoriesResponse;
import com.vawndev.spring_boot_readnovel.Entities.Category;
import com.vawndev.spring_boot_readnovel.Mappers.CategoryMapper;
import com.vawndev.spring_boot_readnovel.Repositories.CategoryRepository;
import com.vawndev.spring_boot_readnovel.Services.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    public CategoriesResponse getCategories() {
        List<Category> category = categoryRepository.findAll();
        CategoriesResponse categoriesResponse = CategoriesResponse.builder().categories(category).build();
        return categoriesResponse;
    }

    @Override
    public void addCategory(String name) {
        Category exstingCategory = categoryRepository.findByName(name).get();
        if (exstingCategory != null) {
            throw new RuntimeException("Category already exists");
        }
        Category category=Category.builder().name(name).build();
        categoryRepository.save(category);
    }

    @Override
    public void RemoveCategory(String id) {
        Category exstingCategory = categoryRepository.findById(id).get();
        if (exstingCategory == null) {
            throw new RuntimeException("Category is not exists");
        }
        exstingCategory.setDeleteAt(LocalDateTime.now());
        categoryRepository.save(exstingCategory);
    }

    @Override
    public void DeleteCategory(String id) {
        Category exstingCategory = categoryRepository.findById(id).get();
        if (exstingCategory == null) {
            throw new RuntimeException("Category is not exists");
        }
        categoryRepository.delete(exstingCategory);
    }

}
