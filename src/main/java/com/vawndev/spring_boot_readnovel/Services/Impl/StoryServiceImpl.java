package com.vawndev.spring_boot_readnovel.Services.Impl;

import com.vawndev.spring_boot_readnovel.Dto.Requests.FILE.ImageCoverRequest;
import com.vawndev.spring_boot_readnovel.Dto.Requests.PageRequest;
import com.vawndev.spring_boot_readnovel.Dto.Requests.Story.ModeratedByAdmin;
import com.vawndev.spring_boot_readnovel.Dto.Requests.Story.StoryCondition;
import com.vawndev.spring_boot_readnovel.Dto.Requests.Story.StoryRequests;
import com.vawndev.spring_boot_readnovel.Dto.Responses.Chapter.ChapterResponses;
import com.vawndev.spring_boot_readnovel.Dto.Responses.PageResponse;
import com.vawndev.spring_boot_readnovel.Dto.Responses.Story.StoriesResponse;
import com.vawndev.spring_boot_readnovel.Dto.Responses.Story.StoryDetailResponses;
import com.vawndev.spring_boot_readnovel.Dto.Responses.Story.StoryResponse;
import com.vawndev.spring_boot_readnovel.Entities.Category;
import com.vawndev.spring_boot_readnovel.Entities.Chapter;
import com.vawndev.spring_boot_readnovel.Entities.Story;
import com.vawndev.spring_boot_readnovel.Entities.User;
import com.vawndev.spring_boot_readnovel.Enum.IS_AVAILBLE;
import com.vawndev.spring_boot_readnovel.Enum.STORY_STATUS;
import com.vawndev.spring_boot_readnovel.Enum.TransactionType;
import com.vawndev.spring_boot_readnovel.Exceptions.AppException;
import com.vawndev.spring_boot_readnovel.Exceptions.ErrorCode;
import com.vawndev.spring_boot_readnovel.Mappers.StoryMapper;
import com.vawndev.spring_boot_readnovel.Repositories.CategoryRepository;
import com.vawndev.spring_boot_readnovel.Repositories.ChapterRepository;
import com.vawndev.spring_boot_readnovel.Repositories.StoryRepository;
import com.vawndev.spring_boot_readnovel.Repositories.UserRepository;
import com.vawndev.spring_boot_readnovel.Services.CloundService;
import com.vawndev.spring_boot_readnovel.Services.StoryService;
import com.vawndev.spring_boot_readnovel.Utils.FileUpload;
import com.vawndev.spring_boot_readnovel.Utils.Help.TokenHelper;
import com.vawndev.spring_boot_readnovel.Utils.PaginationUtil;
import com.vawndev.spring_boot_readnovel.Utils.TimeZoneConvert;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.beans.FeatureDescriptor;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StoryServiceImpl implements StoryService {

    private final StoryRepository storyRepository;
    private final UserRepository userRepository;
    private final ChapterRepository chapterRepository;
    private final StoryMapper storyMapper;
    private final CloundService cloundService;
    private final CategoryRepository categoryRepository;
    private final TokenHelper tokenHelper;


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

    private PageResponse<StoriesResponse> fetchStories(PageRequest req, List<STORY_STATUS> status) {
        Pageable pageable = PaginationUtil.createPageable(req.getPage(), req.getLimit());

        try {
            Page<Story> storyPage = storyRepository.findAccepted(IS_AVAILBLE.ACCEPTED, status, pageable);

            List<StoriesResponse> stories = storyPage.getContent().stream()
                    .map(this::convertToResponse)
                    .collect(Collectors.toList());

            return PageResponse.<StoriesResponse>builder()
                    .data(stories)
                    .page(req.getPage())
                    .limit(req.getLimit())
                    .total(storyPage.getTotalPages())
                    .build();
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi lấy danh sách truyện", e);
        }
    }

    private StoriesResponse convertToResponse(Story story) {
        return StoriesResponse.builder()
                .id(story.getId())
                .title(story.getTitle())
                .type(story.getType())
                .status(story.getStatus())
                .view(story.getViews())
                .coverImage(story.getCoverImage())
                .updatedAt(TimeZoneConvert.convertUtcToUserTimezone(story.getUpdatedAt()))
                .build();
    }


    @Override
    public PageResponse<StoriesResponse> getStories(PageRequest req) {
        List<STORY_STATUS> status = List.of(STORY_STATUS.COMPLETED, STORY_STATUS.UPDATING);
        return fetchStories(req, status);
    }

    @Override
    public PageResponse<StoriesResponse> getStoriesComingSoon(PageRequest req) {
        List<STORY_STATUS> status = List.of(STORY_STATUS.COMING_SOON);
        return fetchStories(req, status);
    }

    @Override
    public PageResponse<StoriesResponse> getStoriesUpdating(PageRequest req) {
        List<STORY_STATUS> status = List.of(STORY_STATUS.UPDATING);
        Pageable pageable = PaginationUtil.createPageable(req.getPage(), req.getLimit());
        try {
            Page<Story> stories=storyRepository.findUpdating(IS_AVAILBLE.ACCEPTED, status, pageable);
            List<StoriesResponse> storyRepositories=stories.stream().map(
                    this::convertToResponse
            ).collect(Collectors.toList());

            return PageResponse.<StoriesResponse>builder()
                    .data(storyRepositories)
                    .page(req.getPage())
                    .limit(req.getLimit())
                    .total(stories.getTotalPages())
                    .build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public List<StoriesResponse> getStoriesRank() {
        List<STORY_STATUS> status = List.of(STORY_STATUS.COMPLETED, STORY_STATUS.UPDATING);
        Pageable pageable = PaginationUtil.createPageable(0,9);
        try {
            List<Story> stories=storyRepository.findTopStories(IS_AVAILBLE.ACCEPTED, status, pageable);
            List<StoriesResponse> storyRepositories=stories.stream().map(story->storyMapper.toStoriesResponse(story)).collect(Collectors.toList());
            return storyRepositories;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
//    @PreAuthorize("hasRole('ADMIN')")
    public PageResponse<StoriesResponse> getStoriesByAdmin(PageRequest req) {
        Pageable pageable = PaginationUtil.createPageable(req.getPage(), req.getLimit());
        Page<Story> storyPage = storyRepository.findAll(pageable);
        List<StoriesResponse> stories = storyPage.getContent().stream().map(this::convertToResponse).toList();
        return PageResponse.<StoriesResponse>builder()
                .data(stories)
                .page(req.getPage())
                .limit(req.getLimit())
                .total(storyPage.getTotalPages())
                .build();
    }


    @Override
//    @PreAuthorize("hasRole('AUTHOR')")
    public void addStory(StoryRequests req, MultipartFile image_cover ,String bearerToken) {
        User author = tokenHelper.getRealAuthorizedUser(req.getEmailAuthor(), bearerToken);
        try{
            ImageCoverRequest imageCoverRequest=new ImageCoverRequest();
            imageCoverRequest.setImage_cover(image_cover);
            List<Category> categories = req.getCategories().stream()
                    .map(categoryId -> categoryRepository.findById(categoryId.getId())
                            .orElseThrow(() -> new AppException(ErrorCode.INVALID_CATE)))
                    .collect(Collectors.toList());

            String CoverImage=cloundService.getUrlCoverAfterUpload(imageCoverRequest);
            Story story = Story
                    .builder()
                    .author(author)
                    .categories(categories)
                    .description(req.getDescription())
                    .isVisibility(false)
                    .isAvailable(IS_AVAILBLE.PENDING)
                    .rate(0)
                    .views(0L)
                    .type(req.getType())
                    .coverImage(CoverImage)
                    .price(BigDecimal.ZERO)
                    .title(req.getTitle())
                    .build();
            storyRepository.save(story);
        } catch (Exception e) {
            throw new RuntimeException("Error while processing file: " + image_cover.getOriginalFilename() + " - " + e.getMessage(), e);
        }
    }


    @Override
//    @PreAuthorize("hasRole('AUTHOR')")
    public void updateStoryByAuthor(StoryRequests req,String id,String bearerToken) {
        User author = tokenHelper.getRealAuthorizedUser(req.getEmailAuthor(), bearerToken);
        Story story = storyRepository.findByIdAndAuthor(id ,author)
                .orElseThrow(() -> new AppException(ErrorCode.INVALID_STORY));
        try {
            BeanUtils.copyProperties(req, story, getNullPropertyNames(req));
            storyRepository.save(story);
        } catch (Exception e) {
            throw new AppException(ErrorCode.INVALID_STORY);
        }
    }

    @Override
    public void updateCoverImage(StoryCondition req, MultipartFile image,String bearerToken) {
        User author = tokenHelper.getRealAuthorizedUser(req.getEmail(), bearerToken);
        Story story = storyRepository.findByIdAndAuthor(req.getId() ,author)
                .orElseThrow(() -> new AppException(ErrorCode.INVALID_STORY));
        try{
            ImageCoverRequest imageCoverRequest=new ImageCoverRequest();
            imageCoverRequest.setImage_cover(image);
            String publicId= FileUpload.extractPublicId(story.getCoverImage());
            if(publicId!=null && !publicId.isEmpty()){
                cloundService.removeUrlOnStory(story.getCoverImage());
            }
            String coverUrl=cloundService.getUrlCoverAfterUpload(imageCoverRequest);

            story.setCoverImage(coverUrl);
            storyRepository.save(story);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    @Override
//    @PreAuthorize("hasRole('ADMIN')")
    public void ModeratedByAdmin(ModeratedByAdmin req) {
        User user = author(req.getEmail());
            Story story=storyRepository.findById(req.getStory_id()).orElseThrow(()->new AppException(ErrorCode.INVALID_STORY));
            try {
                if (req.getIsAvailable()!=null){
                    story.setIsAvailable(req.getIsAvailable());
                    storyRepository.save(story);
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
    }

    @Override
//    @PreAuthorize("hasRole('AUTHOR')")
    public void deleteSoftStory(StoryCondition req,String bearerToken) {
        User author = tokenHelper.getRealAuthorizedUser(req.getEmail(), bearerToken);
        Story story=storyRepository.findByIdAndAuthor(req.getId(),author).orElseThrow(()->new AppException(ErrorCode.INVALID_STORY));
            try {
                    story.setDeleteAt(Instant.now());
                    storyRepository.save(story);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

    }

    @Override
//    @PreAuthorize("hasRole('ADMIN')")
    public void deleteStory(StoryCondition req, String bearerToken) {
        User author = tokenHelper.getRealAuthorizedUser(req.getEmail(), bearerToken);
        Story story =storyRepository.findByIdAndAuthor(req.getId(),author).orElseThrow(()->new AppException(ErrorCode.INVALID_STORY));
        storyRepository.delete(story);
    }


    @Override
    public StoryDetailResponses getStoryById(String id) {
        Story story = storyRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.INVALID_STORY));

        List<Chapter> chapters = chapterRepository.findAllByStoryId(story.getId());

        try {
            StoryResponse storyRes = storyMapper.toStoryResponse(story);
            List<ChapterResponses> chaptersRes = chapters.stream().map(
                    chapter -> ChapterResponses.builder()
                            .content(chapter.getContent())
                            .id(chapter.getId())
                            .price(chapter.getPrice() != null ? chapter.getPrice() : BigDecimal.ZERO)
                            .title(chapter.getTitle())
                            .transactionType(TransactionType.PURCHASE)
                            .build()
            ).collect(Collectors.toList());

            BigDecimal big_price = chaptersRes.stream()
                    .map(chapter -> chapter.getPrice() != null ? chapter.getPrice() : BigDecimal.ZERO)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            return StoryDetailResponses.builder()
                    .chapter(chaptersRes)
                    .author(storyRes.getAuthor())
                    .price(big_price)
                    .title(storyRes.getTitle())
                    .createdAt(storyRes.getCreatedAt())
                    .categories(storyRes.getCategories())
                    .coverImage(storyRes.getCoverImage())
                    .isAvailable(storyRes.getIsAvailable())
                    .rate(storyRes.getRate())
                    .views(storyRes.getViews())
                    .updatedAt(storyRes.getUpdatedAt())
                    .createdAt(storyRes.getCreatedAt())
                    .type(storyRes.getType())
                    .view(storyRes.getView())
                    .description(storyRes.getDescription())
                    .build();
        } catch (Exception e) {
            throw new AppException(ErrorCode.INVALID_STORY);
        }
    }




}
