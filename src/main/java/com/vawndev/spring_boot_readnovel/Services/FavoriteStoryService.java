package com.vawndev.spring_boot_readnovel.Services;

import com.vawndev.spring_boot_readnovel.Dto.Responses.Story.StoriesResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface FavoriteStoryService {
    boolean toggleFavorite(String storyId);
    Page<StoriesResponse> getFavoriteStories(int page, int size);

    boolean isFavoriteStory(String id);
}
