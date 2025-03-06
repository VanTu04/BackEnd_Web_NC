package com.vawndev.spring_boot_readnovel.Services;

import com.vawndev.spring_boot_readnovel.Dto.Requests.PageRequest;
import com.vawndev.spring_boot_readnovel.Dto.Requests.Story.ModeratedByAdmin;
import com.vawndev.spring_boot_readnovel.Dto.Requests.Story.StoryRequests;
import com.vawndev.spring_boot_readnovel.Dto.Responses.PageResponse;
import com.vawndev.spring_boot_readnovel.Dto.Responses.Story.StoriesResponse;
import com.vawndev.spring_boot_readnovel.Dto.Responses.Story.StoryDetailResponses;


public interface StoryService {
    PageResponse<StoriesResponse> getStories(PageRequest req);
    PageResponse<StoriesResponse> getStoriesByAdmin(PageRequest req);
    void addStory(StoryRequests req);
    void updateStoryByAuthor(StoryRequests req);
    void ModeratedByAdmin(ModeratedByAdmin moderatedByAdmin);
    void deleteSoftStory(String email , String id);
    void deleteStory(ModeratedByAdmin req);
    StoryDetailResponses getStoryById(String storyId);

}
