package com.vawndev.spring_boot_readnovel.Services;

import com.vawndev.spring_boot_readnovel.Dto.Requests.Story.ModeratedByAdmin;
import com.vawndev.spring_boot_readnovel.Dto.Requests.Story.StoryCondition;
import com.vawndev.spring_boot_readnovel.Dto.Requests.Story.StoryRequests;
import com.vawndev.spring_boot_readnovel.Dto.Responses.Page.PageResponse;
import com.vawndev.spring_boot_readnovel.Dto.Responses.Story.StoriesResponse;
import com.vawndev.spring_boot_readnovel.Dto.Responses.Story.StoryDetailResponses;


public interface StoryService {
    PageResponse<StoriesResponse> getStories(int page, int limit);
    void addStory(StoryRequests req);
    void updateStoryByVendor(StoryRequests req);
    void ModeratedByAdmin(ModeratedByAdmin moderatedByAdmin);
    void deleteSoftStory(String email , String id);
    void deleteStory(ModeratedByAdmin req);
    StoryDetailResponses getStoryById(String storyId);

}
