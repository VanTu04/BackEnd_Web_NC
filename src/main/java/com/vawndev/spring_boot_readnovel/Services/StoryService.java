package com.vawndev.spring_boot_readnovel.Services;

import com.vawndev.spring_boot_readnovel.Dto.Requests.StoryRequest;
import com.vawndev.spring_boot_readnovel.Dto.Responses.StoryResponse;
import com.vawndev.spring_boot_readnovel.Entities.Story;

import java.util.List;

public interface StoryService {
    StoryResponse addStory(StoryRequest story);

    StoryResponse updateStory(String id, StoryRequest storyDetails);

    void deleteStory(String id);

    List<StoryResponse> getAllStories();
}