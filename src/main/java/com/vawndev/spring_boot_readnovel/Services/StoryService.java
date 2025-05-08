package com.vawndev.spring_boot_readnovel.Services;

import com.vawndev.spring_boot_readnovel.Dto.Requests.PageRequest;
import com.vawndev.spring_boot_readnovel.Dto.Requests.Story.ModeratedByAdmin;
import com.vawndev.spring_boot_readnovel.Dto.Requests.Story.StoryCondition;
import com.vawndev.spring_boot_readnovel.Dto.Requests.Story.StoryRequests;
import com.vawndev.spring_boot_readnovel.Dto.Responses.PageResponse;
import com.vawndev.spring_boot_readnovel.Dto.Responses.Story.StoriesResponse;
import com.vawndev.spring_boot_readnovel.Dto.Responses.Story.StoryDetailResponses;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface StoryService {
    PageResponse<StoriesResponse> getStories(PageRequest req);

    PageResponse<StoriesResponse> getAuthorStories(PageRequest req, String email);

    PageResponse<StoriesResponse> getStoriesComingSoon(PageRequest req);

    PageResponse<StoriesResponse> getStoriesUpdating(PageRequest req);

    PageResponse<StoriesResponse> recommendStories(PageRequest req, String BearerToken);

    PageResponse<StoriesResponse> getMyList(PageRequest req);

    List<StoriesResponse> getStoriesRank();

    PageResponse<StoriesResponse> getStoriesByAdmin(PageRequest req);

    void addStory(StoryRequests req, MultipartFile image);

    void updateStoryByAuthor(StoryRequests req, String id);

    void updateCoverImage(StoryCondition req, MultipartFile image);

    void ModeratedByAdmin(ModeratedByAdmin moderatedByAdmin);

    void deleteSoftStory(StoryCondition req);

    void restoreSoftStory(StoryCondition req);

    void deleteStory(StoryCondition req);

    StoryDetailResponses getStoryById(String id, PageRequest req);

    StoryDetailResponses getMyStory(String id, PageRequest req);

    PageResponse<StoriesResponse> getStoriesTrash(PageRequest req);

    PageResponse<StoriesResponse> getStoriesByAuthorId(PageRequest req, String authorId);

    void toggleVisibilityStory(Boolean isVisibility, String id);

    void updateStoryBanStatus(String id, boolean isBan);
}
