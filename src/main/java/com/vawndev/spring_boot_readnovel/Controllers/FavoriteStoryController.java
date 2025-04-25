package com.vawndev.spring_boot_readnovel.Controllers;

import com.vawndev.spring_boot_readnovel.Dto.Responses.ApiResponse;
import com.vawndev.spring_boot_readnovel.Dto.Responses.Story.StoriesResponse;
import com.vawndev.spring_boot_readnovel.Services.FavoriteStoryService;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/favorites")
public class FavoriteStoryController {
    final FavoriteStoryService favoriteStoryService;

    @PostMapping("/{storyId}")
    public ApiResponse<?> toggleFavorite(@PathVariable String storyId) {
        return ApiResponse.<Boolean>builder().message("Toggle success").result(favoriteStoryService.toggleFavorite(storyId)).build();
    }

    @GetMapping
    public ApiResponse<?> getFavorites(
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "5") @Positive int size
    ) {
        return ApiResponse.<Page<StoriesResponse>>builder().message("Successfully").result(favoriteStoryService.getFavoriteStories(page, size)).build();
    }

    @GetMapping("/stories/{id}")
    public ApiResponse<?> checkIfFavorite(@PathVariable("id") String id) {
        boolean isFavorite = favoriteStoryService.isFavoriteStory(id);
        return ApiResponse.<Boolean>builder().result(isFavorite).build();
    }
}
