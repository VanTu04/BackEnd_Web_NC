package com.vawndev.spring_boot_readnovel.Services;

import com.vawndev.spring_boot_readnovel.Dto.Responses.Story.StoriesResponse;

import java.util.List;

public interface FavoriteStoryService {
    void toggleFavorite(String storyId);
    List<StoriesResponse> getFavoriteStories(int page, int size);

    boolean isFavoriteStory(String id);
}
