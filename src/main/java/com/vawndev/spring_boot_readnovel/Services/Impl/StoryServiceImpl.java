package com.vawndev.spring_boot_readnovel.Services.Impl;

import com.vawndev.spring_boot_readnovel.Dto.Requests.Story.ModeratedByAdmin;
import com.vawndev.spring_boot_readnovel.Dto.Requests.Story.StoryCondition;
import com.vawndev.spring_boot_readnovel.Dto.Requests.Story.StoryRequests;
import com.vawndev.spring_boot_readnovel.Dto.Responses.Chapter.ChapterResponses;
import com.vawndev.spring_boot_readnovel.Dto.Responses.Page.PageResponse;
import com.vawndev.spring_boot_readnovel.Dto.Responses.Story.StoriesResponse;
import com.vawndev.spring_boot_readnovel.Dto.Responses.Story.StoryDetailResponses;
import com.vawndev.spring_boot_readnovel.Dto.Responses.Story.StoryResponse;
import com.vawndev.spring_boot_readnovel.Entities.Chapter;
import com.vawndev.spring_boot_readnovel.Entities.Story;
import com.vawndev.spring_boot_readnovel.Entities.User;
import com.vawndev.spring_boot_readnovel.Enum.ROLE;
import com.vawndev.spring_boot_readnovel.Enum.StoryState;
import com.vawndev.spring_boot_readnovel.Exceptions.AppException;
import com.vawndev.spring_boot_readnovel.Exceptions.ErrorCode;
import com.vawndev.spring_boot_readnovel.Repositories.ChapterReponsitory;
import com.vawndev.spring_boot_readnovel.Repositories.StoryRepository;
import com.vawndev.spring_boot_readnovel.Repositories.UserRepository;
import com.vawndev.spring_boot_readnovel.Services.StoryService;
import com.vawndev.spring_boot_readnovel.Util.PaginationUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.beans.FeatureDescriptor;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StoryServiceImpl implements StoryService {

    private final StoryRepository storyRepository;
    private final UserRepository userRepository;
    private final ChapterReponsitory chapterReponsitory;

    private String[] getNullPropertyNames(Object source) {
        final BeanWrapper wrappedSource = new BeanWrapperImpl(source);
        return Arrays.stream(wrappedSource.getPropertyDescriptors())
                .map(FeatureDescriptor::getName)
                .filter(propertyName -> wrappedSource.getPropertyValue(propertyName) == null)
                .toArray(String[]::new);
    }

    private User author(String email){
        return userRepository.findByEmail(email).orElseThrow(() -> new AppException(ErrorCode.USER_EXISTED));
    }


    @Override
    public PageResponse<StoriesResponse> getStories(int page, int limit) {
        try {
            Pageable pageable = PaginationUtil.createPageable(page, limit);

            Page<Story> storyPage = storyRepository.findAllByStateAndIsApprovedAndIsAvailableAndDeleteAtIsNull(
                    StoryState.PUBLISHED,
                    true,
                    true,
                    pageable
            );
            List<StoriesResponse> stories = storyPage.getContent()
                    .stream()
                    .map(story -> StoriesResponse.builder()
                            .title(story.getTitle())
                            .rate(story.getRate())
                            .views(story.getViews())
                            .view(story.getView())
                            .build())
                    .collect(Collectors.toList());

            return PageResponse.<StoriesResponse>builder()
                    .data(stories)
                    .page(page)
                    .limit(limit)
                    .total((int) storyPage.getTotalElements())  // Đổi getTotalPages() -> getTotalElements()
                    .build();
        } catch (Exception e) {
            throw new AppException(ErrorCode.SERVER_ERROR);
        }
    }


    @Override
    public void addStory(StoryRequests req) {
        User author = author(req.getEmailAuthor());
        try{
            Story story = Story
                    .builder()
                    .author(author)
                    .categories(req.getCategories())
                    .description(req.getDescription())
                    .isApproved(false)
                    .state(req.getState())
                    .rate(0)
                    .view(0L)
                    .views(0)
                    .price(req.getPrice())
                    .state(req.getState())
                    .title(req.getTitle())
                    .price(req.getPrice())
                    .build();
            storyRepository.save(story);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void updateStoryByVendor(StoryRequests req) {
        User author = author(req.getEmailAuthor());
        if(author.getRoles().contains(ROLE.AUTHOR)){
            Story story=storyRepository.findByIdAndAuthor(req.getId(),author).orElseThrow(()->new AppException(ErrorCode.INVALID_STORY));
            try{
                BeanUtils.copyProperties(req, story,getNullPropertyNames(req));
                storyRepository.save(story);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }


    }
    @Override
    public void ModeratedByAdmin(ModeratedByAdmin req) {
        User user = author(req.getEmail());
        if (user.getRoles().contains(ROLE.ADMIN)){
            Story story=storyRepository.findById(req.getStory_id()).orElseThrow(()->new AppException(ErrorCode.INVALID_STORY));
            try {
                if (req.getIsAvailable()!=null){
                    story.setAvailable(req.getIsAvailable());
                    storyRepository.save(story);
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void deleteSoftStory(String email , String id) {
        User user = author(email);
        if (user.getRoles().contains(ROLE.AUTHOR)){
            Story story=storyRepository.findById(id).orElseThrow(()->new AppException(ErrorCode.INVALID_STORY));
            try {
                    story.setDeleteAt(LocalDateTime.now());
                    storyRepository.save(story);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void deleteStory(ModeratedByAdmin req) {
    }

    @Override
    public StoryDetailResponses getStoryById(String id) {
        Story story=storyRepository.findById(id).orElseThrow(()->new AppException(ErrorCode.INVALID_STORY));
        List<Chapter> chapters =chapterReponsitory.findAllByStoryId(story.getId());

        try{
            StoryResponse storyRes= StoryResponse
                    .builder()
                    .author(story.getAuthor())
                    .createdAt(story.getCreatedAt())
                    .updatedAt(story.getUpdatedAt())
                    .price(story.getPrice())
                    .type(story.getType())
                    .views(story.getViews())
                    .view(story.getView())
                    .categories(story.getCategories())
                    .description(story.getDescription())
                    .title(story.getTitle())
                    .build();

            List<ChapterResponses> chaptersRes=chapters.stream().map(chapter->ChapterResponses
                    .builder()
                    .content(chapter.getContent())
                    .images(chapter.getImages())
                    .price(chapter.getPrice())
                    .title(chapter.getTitle())
                    .build()
            ).toList();

            return StoryDetailResponses
                    .builder()
                    .chapter(chaptersRes)
                    .story(storyRes)
                    .build();
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }



}
