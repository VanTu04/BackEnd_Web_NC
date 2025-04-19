package com.vawndev.spring_boot_readnovel.Services.Impl;

import com.vawndev.spring_boot_readnovel.Dto.Responses.Category.CategoryResponse;
import com.vawndev.spring_boot_readnovel.Dto.Responses.Story.StoriesResponse;
import com.vawndev.spring_boot_readnovel.Entities.FavoriteStory;
import com.vawndev.spring_boot_readnovel.Entities.Story;
import com.vawndev.spring_boot_readnovel.Entities.User;
import com.vawndev.spring_boot_readnovel.Exceptions.AppException;
import com.vawndev.spring_boot_readnovel.Exceptions.ErrorCode;
import com.vawndev.spring_boot_readnovel.Repositories.FavoriteStoryRepository;
import com.vawndev.spring_boot_readnovel.Repositories.StoryRepository;
import com.vawndev.spring_boot_readnovel.Repositories.UserRepository;
import com.vawndev.spring_boot_readnovel.Services.FavoriteStoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FavoriteStoryServiceImpl implements FavoriteStoryService {
    private final FavoriteStoryRepository favoriteStoryRepository;
    private final UserRepository userRepository;
    private final StoryRepository storyRepository;


    @Override
//    @PreAuthorize("hasAnyRole('ADMIN', 'AUTHOR', 'ADMIN')")
    public void toggleFavorite(String storyId) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        Story story = storyRepository.findById(storyId)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND, "Story"));

        Optional<FavoriteStory> existingFavorite = favoriteStoryRepository.findByUserAndStory(user, story);

        if (existingFavorite.isPresent()) {
            favoriteStoryRepository.delete(existingFavorite.get()); // Bỏ yêu thích
        } else {
            FavoriteStory favorite = FavoriteStory.builder()
                    .user(user)
                    .story(story)
                    .build();
            favoriteStoryRepository.save(favorite); // Thêm yêu thích
        }
    }

    @Override
    public List<StoriesResponse> getFavoriteStories(int page, int size) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        return favoriteStoryRepository.findAllByUserOrderByCreatedAtDesc(user, pageable)
                .stream()
                .map(favorite -> mapToStoriesResponse(favorite.getStory()))
                .toList();
    }

    @Override
    public boolean isFavoriteStory(String id) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        Story story = storyRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND, "Story"));

        return favoriteStoryRepository.existsByUserAndStory(user, story);
    }

    private StoriesResponse mapToStoriesResponse(Story story) {
        return StoriesResponse.builder()
                .id(story.getId())
                .title(story.getTitle())
                .type(story.getType())
                .view(story.getViews())
                .status(story.getStatus())
                .coverImage(story.getCoverImage())
                .categories(story.getCategories().stream()
                        .map(category -> CategoryResponse.builder()
                                .id(category.getId())
                                .name(category.getName())
                                .build())
                        .toList())
                .build();
    }

}
