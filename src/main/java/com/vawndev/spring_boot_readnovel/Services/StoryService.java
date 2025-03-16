package com.vawndev.spring_boot_readnovel.Services;

import com.vawndev.spring_boot_readnovel.Dto.Requests.PageRequest;
import com.vawndev.spring_boot_readnovel.Dto.Requests.Story.ModeratedByAdmin;
import com.vawndev.spring_boot_readnovel.Dto.Requests.Story.StoryCondition;
import com.vawndev.spring_boot_readnovel.Dto.Requests.Story.StoryRequests;
import com.vawndev.spring_boot_readnovel.Dto.Responses.PageResponse;
import com.vawndev.spring_boot_readnovel.Dto.Responses.Story.StoriesResponse;
import com.vawndev.spring_boot_readnovel.Dto.Responses.Story.StoryDetailResponses;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


public interface StoryService {
    PageResponse<StoriesResponse> getStories(PageRequest req);
    PageResponse<StoriesResponse> getStoriesComingSoon(PageRequest req);
    PageResponse<StoriesResponse> getStoriesUpdating(PageRequest req);
    List<StoriesResponse> getStoriesRank();
    PageResponse<StoriesResponse> getStoriesByAdmin(PageRequest req);
    void addStory(StoryRequests req, MultipartFile image, String bearerToken);
    void updateStoryByAuthor(StoryRequests req,String id,String authHeader);
    void updateCoverImage(StoryCondition req, MultipartFile image,String bearerToken);
    void ModeratedByAdmin(ModeratedByAdmin moderatedByAdmin);
    void deleteSoftStory(StoryCondition req, String bearerToken);
    void deleteStory(StoryCondition req,String bearerToken);
    StoryDetailResponses getStoryById(String storyId);

}
