package com.vawndev.spring_boot_readnovel.Services.Impl;

import com.vawndev.spring_boot_readnovel.Dto.Requests.FILE.ImageCoverRequest;
import com.vawndev.spring_boot_readnovel.Dto.Requests.FILE.ImageFileRequest;
import com.vawndev.spring_boot_readnovel.Dto.Requests.PageRequest;
import com.vawndev.spring_boot_readnovel.Dto.Requests.Story.ModeratedByAdmin;
import com.vawndev.spring_boot_readnovel.Dto.Requests.Story.StoryRequests;
import com.vawndev.spring_boot_readnovel.Dto.Responses.Chapter.ChapterResponses;
import com.vawndev.spring_boot_readnovel.Dto.Responses.PageResponse;
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
import com.vawndev.spring_boot_readnovel.Mappers.ChapterMapper;
import com.vawndev.spring_boot_readnovel.Mappers.StoryMapper;
import com.vawndev.spring_boot_readnovel.Repositories.ChapterRepository;
import com.vawndev.spring_boot_readnovel.Repositories.StoryRepository;
import com.vawndev.spring_boot_readnovel.Repositories.UserRepository;
import com.vawndev.spring_boot_readnovel.Services.CloundService;
import com.vawndev.spring_boot_readnovel.Services.StoryService;
import com.vawndev.spring_boot_readnovel.Util.PaginationUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.beans.FeatureDescriptor;
import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StoryServiceImpl implements StoryService {

    private final StoryRepository storyRepository;
    private final UserRepository userRepository;
    private final ChapterRepository chapterRepository;
    private final StoryMapper storyMapper;
    private final ChapterMapper chapterMapper;
    private final CloundService cloundService;


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
    public PageResponse<StoriesResponse> getStories(PageRequest req) {
        try {
            Pageable pageable = PaginationUtil.createPageable(req.getPage(), req.getLimit());

            Page<Story> storyPage = storyRepository.findAll(pageable);
            List<StoriesResponse> stories = storyPage.getContent()
                    .stream()
                    .map(story -> StoriesResponse.builder()
                            .title(story.getTitle())
                            .rate(story.getRate())
                            .views(story.getViews())
                            .view(story.getView())
                            .coverImage(story.getCoverImage())
                            .id(story.getId())
                            .build())
                    .collect(Collectors.toList());

            return PageResponse.<StoriesResponse>builder()
                    .data(stories)
                    .page(req.getPage())
                    .limit(req.getLimit())
                    .total(storyPage.getTotalPages())
                    .build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
//    @PreAuthorize("hasRole('ADMIN')")
    public PageResponse<StoriesResponse> getStoriesByAdmin(PageRequest req) {
        Pageable pageable = PaginationUtil.createPageable(req.getPage(), req.getLimit());
        Page<Story> storyPage = storyRepository.findAll(pageable);
        List<StoriesResponse> stories = storyPage.getContent().stream().map(storyMapper::toStoriesResponse).toList();
        return PageResponse.<StoriesResponse>builder()
                .data(stories)
                .page(req.getPage())
                .limit(req.getLimit())
                .total(storyPage.getTotalPages())
                .build();
    }


    @Override
    public void addStory(StoryRequests req, MultipartFile image_cover) {
        User author = author(req.getEmailAuthor());

        try{
            ImageCoverRequest imageCoverRequest=new ImageCoverRequest();
            imageCoverRequest.setImage_cover(image_cover);
            String CoverImage=cloundService.getUrlCoverAfterUpload(imageCoverRequest);
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
                    .coverImage(CoverImage)
                    .price(req.getPrice())
                    .state(req.getState())
                    .title(req.getTitle())
                    .price(req.getPrice())
                    .build();
            storyRepository.save(story);
        } catch (Exception e) {
            throw new RuntimeException("Error while processing file: " + image_cover.getOriginalFilename() + " - " + e.getMessage(), e);
        }
    }


    @Override
//    @PreAuthorize("hasRole('AUTHOR')")
    public void updateStoryByAuthor(StoryRequests req,String id,MultipartFile image) {
        User author = author(req.getEmailAuthor());
        Story story = storyRepository.findByIdAndAuthor(id ,author)
                .orElseThrow(() -> new AppException(ErrorCode.INVALID_STORY));
        try {
            if (image != null) {
                ImageFileRequest imagereq=new ImageFileRequest();
                imagereq.setFile((List<MultipartFile>) image);
                String CoverImage=cloundService.getUrlAfterUpload(imagereq).get(0);
                story.setCoverImage(CoverImage);
            }
            BeanUtils.copyProperties(req, story, getNullPropertyNames(req));
            storyRepository.save(story);
        } catch (Exception e) {
            throw new AppException(ErrorCode.INVALID_STORY);
        }
    }



    @Override
//    @PreAuthorize("hasRole('ADMIN')")
    public void ModeratedByAdmin(ModeratedByAdmin req) {
        User user = author(req.getEmail());
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

    @Override
//    @PreAuthorize("hasRole('AUTHOR')")
    public void deleteSoftStory(String email , String id) {
            Story story=storyRepository.findById(id).orElseThrow(()->new AppException(ErrorCode.INVALID_STORY));
            try {
                    story.setDeleteAt(LocalDateTime.now());
                    storyRepository.save(story);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

    }

    @Override
//    @PreAuthorize("hasRole('ADMIN')")
    public void deleteStory(ModeratedByAdmin req) {
        Story story =storyRepository.findById(req.getStory_id()).orElseThrow(()->new AppException(ErrorCode.INVALID_STORY));
        storyRepository.delete(story);
    }

    @Override
    public StoryDetailResponses getStoryById(String id) {
        Story story=storyRepository.findById(id).orElseThrow(()->new AppException(ErrorCode.INVALID_STORY));
        List<Chapter> chapters = chapterRepository.findAllByStoryId(story.getId());

        try{
            StoryResponse storyRes= storyMapper.toStoryResponse(story);
            List<ChapterResponses> chaptersRes=chapters.stream().map(chapter->chapterMapper.toChapterResponses(chapter)).collect(Collectors.toList());
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
