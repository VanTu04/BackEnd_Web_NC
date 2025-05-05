package com.vawndev.spring_boot_readnovel.Services.Impl;

import com.vawndev.spring_boot_readnovel.Dto.Requests.Category.CategoryRequests;
import com.vawndev.spring_boot_readnovel.Dto.Responses.Category.CategoriesResponse;
import com.vawndev.spring_boot_readnovel.Dto.Responses.Category.CategoryResponse;
import com.vawndev.spring_boot_readnovel.Entities.Category;
import com.vawndev.spring_boot_readnovel.Exceptions.AppException;
import com.vawndev.spring_boot_readnovel.Exceptions.ErrorCode;
import com.vawndev.spring_boot_readnovel.Mappers.CategoryMapper;
import com.vawndev.spring_boot_readnovel.Repositories.CategoryRepository;
import com.vawndev.spring_boot_readnovel.Services.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    public CategoriesResponse getCategories() {
        List<Category> categories = categoryRepository.findAll();
        CategoriesResponse categoriesResponse = CategoriesResponse.builder()
                .categories(categories.stream().map(category -> CategoryResponse
                        .builder()
                        .id(category.getId())
                        .name(category.getName())
                        .build()).collect(Collectors.toList()))
                .build();
        return categoriesResponse;
    }

    @Override
    @PreAuthorize("hasAuthority('ADMIN')")
    public void addCategory(String name) {
        categoryRepository.findByName(name).ifPresent(category -> {
            throw new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION);
        });

        Category category = Category.builder().name(name).build();
        categoryRepository.save(category);
    }

    @Override
    @PreAuthorize("hasAuthority('ADMIN')")
    public void RemoveCategory(String id) {
        Category exstingCategory = categoryRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.INVALID_CATE));
        if (exstingCategory == null) {
            throw new AppException(ErrorCode.USER_NOT_EXISTED);

        }
        exstingCategory.setDeleteAt(Instant.now());
        categoryRepository.save(exstingCategory);
    }

    @Override
    @PreAuthorize("hasAuthority('ADMIN')")
    public void DeleteCategory(String id) {
        Category exstingCategory = categoryRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.INVALID_CATE));
        if (exstingCategory == null) {
            throw new AppException(ErrorCode.USER_NOT_EXISTED);
        }
        categoryRepository.delete(exstingCategory);
    }

    @Override
    @PreAuthorize("hasAuthority('ADMIN')")
    public void UpdateCategory(CategoryRequests req) {
        Category exstingCategory = categoryRepository.findById(req.getId())
                .orElseThrow(() -> new AppException(ErrorCode.INVALID_CATE));
        if (exstingCategory == null) {
            throw new AppException(ErrorCode.INVALID_DOB);
        }
        if (req.getName() != null) {
            exstingCategory.setName(req.getName());
            categoryRepository.save(exstingCategory);
        }
    }

}
