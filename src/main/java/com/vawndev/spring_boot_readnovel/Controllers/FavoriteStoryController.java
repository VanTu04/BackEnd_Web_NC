package com.vawndev.spring_boot_readnovel.Controllers;

import com.vawndev.spring_boot_readnovel.Dto.Responses.ApiResponse;
import com.vawndev.spring_boot_readnovel.Dto.Responses.Story.StoriesResponse;
import com.vawndev.spring_boot_readnovel.Services.FavoriteStoryService;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/favorites")
public class FavoriteStoryController {
    final FavoriteStoryService favoriteStoryService;

    @PostMapping("/{storyId}")
    public ApiResponse<?> toggleFavorite(@PathVariable String storyId) {
        favoriteStoryService.toggleFavorite(storyId);
        return ApiResponse.<String>builder().message("Toggle success").build();
    }

    @GetMapping
    public ApiResponse<?> getFavorites(
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "5") @Positive int size
    ) {
        List<StoriesResponse> favorites = favoriteStoryService.getFavoriteStories(page, size);
        return ApiResponse.<List<StoriesResponse>>builder().message("Successfully").result(favorites).build();
    }

    @GetMapping("/stories/{id}")
    public ApiResponse<?> checkIfFavorite(@PathVariable("id") String id) {
        boolean isFavorite = favoriteStoryService.isFavoriteStory(id);
        return ApiResponse.<Boolean>builder().result(isFavorite).build();
    }
}
