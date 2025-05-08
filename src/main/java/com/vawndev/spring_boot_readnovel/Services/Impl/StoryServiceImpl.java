package com.vawndev.spring_boot_readnovel.Services.Impl;

import com.vawndev.spring_boot_readnovel.Dto.Requests.FILE.ImageCoverRequest;
import com.vawndev.spring_boot_readnovel.Dto.Requests.PageRequest;
import com.vawndev.spring_boot_readnovel.Dto.Requests.Story.ModeratedByAdmin;
import com.vawndev.spring_boot_readnovel.Dto.Requests.Story.StoryCondition;
import com.vawndev.spring_boot_readnovel.Dto.Requests.Story.StoryRequests;
import com.vawndev.spring_boot_readnovel.Dto.Responses.Category.CategoryResponse;
import com.vawndev.spring_boot_readnovel.Dto.Responses.Chapter.ChapterResponseDetail;
import com.vawndev.spring_boot_readnovel.Dto.Responses.Chapter.ChapterResponsePurchase;
import com.vawndev.spring_boot_readnovel.Dto.Responses.PageResponse;
import com.vawndev.spring_boot_readnovel.Dto.Responses.Story.StoriesResponse;
import com.vawndev.spring_boot_readnovel.Dto.Responses.Story.StoryDetailResponses;
import com.vawndev.spring_boot_readnovel.Dto.Responses.Story.StoryResponse;
import com.vawndev.spring_boot_readnovel.Dto.Responses.User.UserResponse;
import com.vawndev.spring_boot_readnovel.Entities.*;
import com.vawndev.spring_boot_readnovel.Enum.IS_AVAILBLE;
import com.vawndev.spring_boot_readnovel.Enum.STORY_STATUS;
import com.vawndev.spring_boot_readnovel.Enum.SUBSCRIPTION_TYPE;
import com.vawndev.spring_boot_readnovel.Enum.TransactionType;
import com.vawndev.spring_boot_readnovel.Exceptions.AppException;
import com.vawndev.spring_boot_readnovel.Exceptions.ErrorCode;
import com.vawndev.spring_boot_readnovel.Mappers.CategoryMapper;
import com.vawndev.spring_boot_readnovel.Mappers.StoryMapper;
import com.vawndev.spring_boot_readnovel.Repositories.*;
import com.vawndev.spring_boot_readnovel.Services.ChapterService;
import com.vawndev.spring_boot_readnovel.Services.CloundService;
import com.vawndev.spring_boot_readnovel.Services.HistoryReadingService;
import com.vawndev.spring_boot_readnovel.Services.StoryService;
import com.vawndev.spring_boot_readnovel.Utils.FileUpload;
import com.vawndev.spring_boot_readnovel.Utils.Help.TokenHelper;
import com.vawndev.spring_boot_readnovel.Utils.Help.UserHelper;
import com.vawndev.spring_boot_readnovel.Utils.JwtUtils;
import com.vawndev.spring_boot_readnovel.Utils.PaginationUtil;
import com.vawndev.spring_boot_readnovel.Utils.TimeZoneConvert;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.beans.FeatureDescriptor;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StoryServiceImpl implements StoryService {

        private final StoryRepository storyRepository;
        private final UserRepository userRepository;
        private final ChapterRepository chapterRepository;
        private final StoryMapper storyMapper;
        private final CloundService cloundService;
        private final ChapterService chapterService;
        private final CategoryMapper categoryMapper;
        private final CategoryRepository categoryRepository;
        private final TokenHelper tokenHelper;
        private final JwtUtils jwtUtils;
        private final ReadingHistoryRepository readingHistoryRepository;

        private User getAuthenticatedUser() {
                return tokenHelper.getUserO2Auth();
        }

        private String[] getNullPropertyNames(Object source) {
                final BeanWrapper wrappedSource = new BeanWrapperImpl(source);
                return Arrays.stream(wrappedSource.getPropertyDescriptors())
                                .map(FeatureDescriptor::getName)
                                .filter(propertyName -> wrappedSource.getPropertyValue(propertyName) == null)
                                .toArray(String[]::new);
        }

        private PageResponse<StoriesResponse> fetchStories(PageRequest req, List<STORY_STATUS> status) {
                Pageable pageable = PaginationUtil.createPageable(req.getPage(), req.getLimit());

                try {
                        Page<Story> storyPage = storyRepository.findAccepted(IS_AVAILBLE.ACCEPTED, status, pageable);
                        List<StoriesResponse> stories = storyPage.getContent().stream()
                                        .map(
                                                        str -> {
                                                                List<BigDecimal> prices = chapterRepository
                                                                                .findChaptersByStory(str.getId());
                                                                BigDecimal bigPrice = BigDecimal.ZERO;

                                                                for (BigDecimal price : prices) {
                                                                        bigPrice = bigPrice.add(price);
                                                                }

                                                                return StoriesResponse.builder()
                                                                                .id(str.getId())
                                                                                .categories(str.getCategories().stream()
                                                                                                .map(ct -> categoryMapper
                                                                                                                .toCategoryResponse(
                                                                                                                                ct))
                                                                                                .collect(Collectors
                                                                                                                .toList()))
                                                                                .coverImage(str.getCoverImage())
                                                                                .title(str.getTitle())
                                                                                .type(str.getType())
                                                                                .status(str.getStatus())
                                                                                .view(str.getViews())
                                                                                .price(bigPrice)
                                                                                .build();

                                                        })
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
                                .categories(story.getCategories() != null ? story.getCategories().stream()
                                                .map(cate -> CategoryResponse
                                                                .builder()
                                                                .name(cate.getName())
                                                                .id(cate.getId())
                                                                .build())
                                                .collect(Collectors.toList()) : null)
                                .isAvailble(story.getIsAvailable())
                                .email(story.getAuthor().getEmail())
                                .status(story.getStatus())
                                .isAvailble(story.getIsAvailable())
                                .view(story.getViews())
                                .coverImage(story.getCoverImage())
                                .updatedAt(TimeZoneConvert.convertUtcToUserTimezone(story.getUpdatedAt()))
                                .isBanned(story.isBanned())
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
        public PageResponse<StoriesResponse> getAuthorStories(PageRequest req, String email) {
                Pageable pageable = PaginationUtil.createPageable(req.getPage(), req.getLimit());
                Page<Story> storyPage = storyRepository.findByAuthor(email, pageable);
                List<StoriesResponse> stories = storyPage.getContent().stream()
                                .map(this::convertToResponse)
                                .collect(Collectors.toList());

                return PageResponse.<StoriesResponse>builder()
                                .data(stories)
                                .page(req.getPage())
                                .limit(req.getLimit())
                                .total(storyPage.getTotalPages())
                                .build();
        }

        @Override
        public PageResponse<StoriesResponse> getStoriesUpdating(PageRequest req) {
                List<STORY_STATUS> status = List.of(STORY_STATUS.UPDATING);
                Pageable pageable = PaginationUtil.createPageable(req.getPage(), req.getLimit());
                try {
                        Page<Story> stories = storyRepository.findUpdating(IS_AVAILBLE.ACCEPTED, status, pageable);
                        List<StoriesResponse> storyRepositories = stories.stream().map(
                                        this::convertToResponse).collect(Collectors.toList());

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

        @Override
        public PageResponse<StoriesResponse> recommendStories(PageRequest req, String BearerToken) {
                Pageable pageable = PaginationUtil.createPageable(req.getPage(), req.getLimit());
                List<STORY_STATUS> status = List.of(STORY_STATUS.COMPLETED, STORY_STATUS.UPDATING);

                User user = Optional.ofNullable(BearerToken)
                                .filter(token -> !token.isEmpty())
                                .map(token -> jwtUtils.validToken(tokenHelper.getTokenInfo(token)))
                                .orElse(null);

                // Nếu không có user → lấy truyện nhiều lượt xem nhất
                if (user == null) {
                        return getPageResponse(req,
                                        storyRepository.findMostViews(IS_AVAILBLE.ACCEPTED, status, pageable));
                }

                // Lấy lịch sử đọc
                Page<ReadingHistory> storiesPage = readingHistoryRepository.findByUser(user, pageable);
                Map<String, Integer> categoriesCount = new HashMap<>();

                // Đếm số lần đọc theo thể loại
                storiesPage.getContent().forEach(readingHistory -> readingHistory.getStory().getCategories()
                                .forEach(category -> categoriesCount.merge(category.getId(), 1, Integer::sum)));

                // Sắp xếp thể loại theo số lần đọc giảm dần
                List<String> mostCategories = categoriesCount.entrySet().stream()
                                .sorted(Map.Entry.<String, Integer>comparingByValue(Comparator.reverseOrder()))
                                .map(Map.Entry::getKey)
                                .collect(Collectors.toList());

                // Get stories list base the most category suggest or fallback to the most
                // stories list
                Page<Story> storyList = !mostCategories.isEmpty()
                                ? storyRepository.findAllByCategoriesIn(mostCategories, pageable)
                                : storyRepository.findMostViews(IS_AVAILBLE.ACCEPTED, status, pageable);

                return getPageResponse(req, storyList);
        }

        @Override
        public PageResponse<StoriesResponse> getMyList(PageRequest req) {
                User user = getAuthenticatedUser();
                Pageable pageable = PaginationUtil.createPageable(req.getPage(), req.getLimit());
                Page<Story> story = storyRepository.findByAuthorId(user.getId(), pageable);
                List<StoriesResponse> stories = story.getContent().stream().map(str -> StoriesResponse.builder()
                                .id(str.getId())
                                .title(str.getTitle())
                                .type(str.getType())
                                .categories(str.getCategories() != null ? str.getCategories().stream()
                                                .map(cate -> CategoryResponse
                                                                .builder()
                                                                .name(cate.getName())
                                                                .id(cate.getId())
                                                                .build())
                                                .collect(Collectors.toList()) : null)
                                .status(str.getStatus())
                                .view(str.getViews())
                                .coverImage(str.getCoverImage())
                                .updatedAt(TimeZoneConvert.convertUtcToUserTimezone(str.getUpdatedAt()))
                                .isVisibility(str.isVisibility())
                                .isAvailble(str.getIsAvailable())
                                .build())

                                .collect(Collectors.toList());
                return PageResponse.<StoriesResponse>builder()
                                .data(stories)
                                .page(req.getPage())
                                .limit(req.getLimit())
                                .build();
        }

        private PageResponse<StoriesResponse> getPageResponse(PageRequest req, Page<Story> storyList) {
                List<StoriesResponse> result = storyList.getContent().stream()
                                .map(storyMapper::toStoriesResponse)
                                .collect(Collectors.toList());

                return PageResponse.<StoriesResponse>builder()
                                .page(req.getPage())
                                .limit(req.getLimit())
                                .total(storyList.getTotalPages())
                                .data(result)
                                .build();
        }

        public List<StoriesResponse> getStoriesRank() {
                List<STORY_STATUS> status = List.of(STORY_STATUS.COMPLETED, STORY_STATUS.UPDATING);
                Pageable pageable = PaginationUtil.createPageable(0, 9);
                try {
                        List<Story> stories = storyRepository.findTopStories(IS_AVAILBLE.ACCEPTED, status, pageable);
                        List<StoriesResponse> storyRepositories = stories.stream()
                                        .map(story -> storyMapper.toStoriesResponse(story))
                                        .collect(Collectors.toList());
                        return storyRepositories;
                } catch (Exception e) {
                        throw new RuntimeException(e);
                }
        }

        @Override
        @PreAuthorize("hasAuthority('ADMIN')")
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
        @PreAuthorize("hasAuthority('AUTHOR')")
        public void addStory(StoryRequests req, MultipartFile image_cover) {
                User author = getAuthenticatedUser();
                try {
                        ImageCoverRequest imageCoverRequest = new ImageCoverRequest();
                        imageCoverRequest.setImage_cover(image_cover);
                        Set<String> seenIds = new HashSet<>();
                        List<Category> uniqueCategories = req.getCategories().stream()
                                        .map(categoryId -> categoryRepository.findById(categoryId.getId()).orElse(null))
                                        .filter(Objects::nonNull) // remove element null
                                        .filter(category -> seenIds.add(category.getId())) // get only the first element
                                                                                           // with the previous
                                                                                           // id
                                        .collect(Collectors.toList());

                        String CoverImage = cloundService.getUrlCoverAfterUpload(imageCoverRequest);
                        Story story = Story
                                        .builder()
                                        .author(author)
                                        .categories(uniqueCategories)
                                        .description(req.getDescription())
                                        .isVisibility(false)
                                        .isBanned(false)
                                        .isAvailable(IS_AVAILBLE.PENDING)
                                        .status(STORY_STATUS.COMING_SOON)
                                        .rate(0)
                                        .views(0L)
                                        .type(req.getType())
                                        .createdAt(Instant.now())
                                        .updatedAt(Instant.now())
                                        .coverImage(CoverImage)
                                        .title(req.getTitle())
                                        .build();
                        storyRepository.save(story);
                } catch (Exception e) {
                        // throw new AppException(ErrorCode.NOT_FOUND, "will be not be large than 2MB
                        // and only jpg,png,jpeg ");
                        throw new RuntimeException(e.getMessage());
                }
        }

        @Override
        @PreAuthorize("hasAuthority('AUTHOR')")
        public void updateStoryByAuthor(StoryRequests req, String id) {
                User author = getAuthenticatedUser();
                Story story = storyRepository.findByIdAndAuthor(id, author.getId())
                                .orElseThrow(() -> new AppException(ErrorCode.INVALID_STORY, "Invalid  story"));
                try {
                        BeanUtils.copyProperties(req, story, getNullPropertyNames(req));
                        storyRepository.save(story);
                } catch (Exception e) {
                        throw new AppException(ErrorCode.INVALID_STORY);
                }
        }

        @Override
        public void updateCoverImage(StoryCondition req, MultipartFile image) {
                User author = getAuthenticatedUser();
                Story story = storyRepository.findByIdAndAuthor(req.getId(), author.getId())
                                .orElseThrow(() -> new AppException(ErrorCode.INVALID_STORY));
                try {
                        ImageCoverRequest imageCoverRequest = new ImageCoverRequest();
                        imageCoverRequest.setImage_cover(image);
                        String publicId = FileUpload.extractPublicId(story.getCoverImage());
                        cloundService.removeUrlOnStory(story.getCoverImage());
                        String coverUrl = cloundService.getUrlCoverAfterUpload(imageCoverRequest);

                        story.setCoverImage(coverUrl);
                        storyRepository.save(story);
                } catch (Exception e) {
                        throw new RuntimeException(e);
                }
        }

        @Override
        @PreAuthorize("hasAuthority('ADMIN')")
        public void ModeratedByAdmin(ModeratedByAdmin req) {
                getAuthenticatedUser();
                Story story = storyRepository.findById(req.getStory_id())
                                .orElseThrow(() -> new AppException(ErrorCode.INVALID_STORY));
                try {
                        if (req.getIsAvailable() != null) {
                                story.setIsAvailable(req.getIsAvailable());
                                storyRepository.save(story);
                        }
                } catch (Exception e) {
                        throw new RuntimeException(e);
                }
        }

        @Override
        @PreAuthorize("hasAuthority('AUTHOR')")
        public void deleteSoftStory(StoryCondition req) {
                User author = getAuthenticatedUser();
                storyRepository.toggleDeleteStory(req.getId(), author.getId(), Instant.now());
        }

        @Override
        @PreAuthorize("hasAuthority('AUTHOR')")
        public void restoreSoftStory(StoryCondition req) {
                User author = getAuthenticatedUser();
                storyRepository.toggleDeleteStory(req.getId(), author.getId(), null);
        }

        @Override
        @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('AUTHOR')")
        public void deleteStory(StoryCondition req) {
                User author = getAuthenticatedUser();
                Story story;

                if (author.getRoles().contains("AUTHOR")) {
                        story = storyRepository.findByIdAndAuthor(req.getId(), author.getId())
                                        .orElseThrow(() -> new AppException(ErrorCode.INVALID_STORY));
                } else {
                        story = storyRepository.findById(req.getId())
                                        .orElseThrow(() -> new AppException(ErrorCode.INVALID_STORY));
                }

                List<String> chapterIds = chapterRepository.findChapterIdByStory(req.getId());
                chapterIds.forEach(chapterId -> chapterService.deleteChapter(chapterId));
                storyRepository.delete(story);
        }

        private final PurchaseHistoryRepository purchaseHistoryRepository;

        @Override
        public StoryDetailResponses getStoryById(String id, PageRequest req) {

                Story story = storyRepository.findByAcceptId(id)
                                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND, "Story"));
                Pageable pageable = PaginationUtil.createPageable(req.getPage(), req.getLimit());
                Page<Chapter> chapters = chapterRepository.findAllByStoryId(story.getId(), pageable);
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                User user = null;
                if (authentication != null) {
                        String email = tokenHelper.getUserEmail();
                        try {
                                user = userRepository.findByEmail(email)
                                                .orElseThrow(() -> new AppException(ErrorCode.USER_EXISTED));
                        } catch (AppException e) {
                                user = null;
                        }
                }
                User finalUser = user;
                boolean isAuthor = user != null && user.getId().equals(story.getAuthor().getId());

                UserResponse auth = UserResponse.builder()
                                .email(story.getAuthor().getEmail())
                                .fullName(story.getAuthor().getFullName())
                                .imageUrl(story.getAuthor().getImageUrl())
                                .build();

                StoryResponse storyRes = StoryResponse
                                .builder()
                                .title(story.getTitle())
                                .author(auth)
                                .coverImage(story.getCoverImage())
                                .categories(story.getCategories().stream()
                                                .map(c -> CategoryResponse.builder().id(c.getId()).name(c.getName())
                                                                .build())
                                                .toList())
                                .createdAt(story.getCreatedAt())
                                .description(id)
                                .isAvailable(story.getIsAvailable())
                                .views(Math.toIntExact(story.getViews()))
                                .type(story.getType())
                                .status(story.getStatus())
                                .description(story.getDescription())
                                .isAvailable(story.getIsAvailable())
                                .build();

                List<String> chapterIds = finalUser != null
                                ? readingHistoryRepository.findReadingChapters(story.getId(), finalUser.getId())
                                                .stream()
                                                .map(Chapter::getId)
                                                .collect(Collectors.toList())
                                : new ArrayList<>();

                List<ChapterResponsePurchase> chaptersRes = chapters.getContent().stream()
                                .map(ch -> {
                                        Optional<PurchaseHistory> purchaseHistoryOptional = finalUser != null
                                                        ? purchaseHistoryRepository
                                                                        .findByChapterAndUser(ch.getId(),
                                                                                        finalUser.getId())
                                                        : null;

                                        BigDecimal priceForUser = isAuthor ? BigDecimal.ZERO
                                                        : finalUser != null && (finalUser.getSubscription() != null
                                                                        || purchaseHistoryOptional.isPresent())
                                                                                        ? BigDecimal.ZERO
                                                                                        : ch.getPrice();
                                        return ChapterResponseDetail.builder()
                                                        .content(ch.getContent())
                                                        .id(ch.getId())
                                                        .price(priceForUser)
                                                        .isRead(chapterIds.contains(ch.getId()))
                                                        .createdAt(TimeZoneConvert
                                                                        .convertUtcToUserTimezone(ch.getCreatedAt()))
                                                        .title(ch.getTitle())
                                                        .views(ch.getViews())
                                                        .transactionType(purchaseHistoryOptional != null
                                                                        ? TransactionType.DEPOSIT
                                                                        : TransactionType.PURCHASE)
                                                        .build();
                                })
                                .collect(Collectors.toList());

                BigDecimal bigPrice = chaptersRes.stream()
                                .map(ChapterResponsePurchase::getPrice)
                                .reduce(BigDecimal.ZERO, BigDecimal::add);

                // Trả về Response
                return StoryDetailResponses.builder()
                                .chapter(chaptersRes)
                                .author(storyRes.getAuthor())
                                .continueReading(!chapterIds.isEmpty() ? chapterIds.get(0) : null)
                                .price(bigPrice)
                                .title(storyRes.getTitle())
                                .createdAt(storyRes.getCreatedAt())
                                .updatedAt(storyRes.getUpdatedAt())
                                .categories(storyRes.getCategories())
                                .coverImage(storyRes.getCoverImage())
                                .isAvailable(storyRes.getIsAvailable())
                                .rate(storyRes.getRate())
                                .views(storyRes.getViews())
                                .type(storyRes.getType())
                                .description(storyRes.getDescription())
                                .status(storyRes.getStatus())
                                .build();
        }

        @Override
        @PreAuthorize("hasAuthority('AUTHOR')")
        public StoryDetailResponses getMyStory(String id, PageRequest req) {
                User user = getAuthenticatedUser();
                Story story = storyRepository.findByMyAcceptId(id)
                                .orElseThrow(() -> new AppException(ErrorCode.INVALID_STORY));
                Pageable pageable = PaginationUtil.createPageable(req.getPage(), req.getLimit());
                Page<Chapter> chapters = chapterRepository.findAllByStoryId(story.getId(), pageable);

                StoryResponse storyRes = storyMapper.toStoryResponse(story);
                boolean isAuthor = user.getId().equals(story.getAuthor().getId());

                List<ChapterResponsePurchase> chaptersRes = chapters.getContent().stream()
                                .map(chapter -> ChapterResponseDetail.builder()
                                                .content(chapter.getContent())
                                                .createdAt(TimeZoneConvert
                                                                .convertUtcToUserTimezone(chapter.getCreatedAt()))
                                                .id(chapter.getId())
                                                .price(isAuthor ? BigDecimal.ZERO : chapter.getPrice())
                                                .isRead(false)
                                                .title(chapter.getTitle())
                                                .views(chapter.getViews())
                                                .transactionType(isAuthor ? null : TransactionType.DEPOSIT)
                                                .build())
                                .collect(Collectors.toList());

                BigDecimal bigPrice = chaptersRes.stream()
                                .map(ChapterResponsePurchase::getPrice)
                                .reduce(BigDecimal.ZERO, BigDecimal::add);

                // Trả về Response
                return StoryDetailResponses.builder()
                                .chapter(chaptersRes)
                                .author(storyRes.getAuthor())
                                .price(bigPrice)
                                .title(storyRes.getTitle())
                                .createdAt(storyRes.getCreatedAt())
                                .updatedAt(storyRes.getUpdatedAt())
                                .categories(storyRes.getCategories())
                                .coverImage(storyRes.getCoverImage())
                                .isAvailable(storyRes.getIsAvailable())
                                .rate(storyRes.getRate())
                                .views(storyRes.getViews())
                                .type(storyRes.getType())
                                .description(storyRes.getDescription())
                                .status(storyRes.getStatus())
                                .build();
        }

        @Override
        @PreAuthorize("hasAuthority('AUTHOR')")
        public PageResponse<StoriesResponse> getStoriesTrash(PageRequest req) {
                Pageable pageable = PaginationUtil.createPageable(req.getPage(), req.getLimit());
                User user = getAuthenticatedUser();
                Page<Story> storyPage = storyRepository.findAllTrashByAuthor(user.getId(), pageable);
                List<StoriesResponse> stories = storyPage.getContent().stream().map(this::convertToResponse).toList();
                return PageResponse.<StoriesResponse>builder()
                                .data(stories)
                                .page(req.getPage())
                                .limit(req.getLimit())
                                .total(storyPage.getTotalPages())
                                .build();
        }

        @Override
        public PageResponse<StoriesResponse> getStoriesByAuthorId(PageRequest req, String idStory) {
                Story s = storyRepository.findByAcceptId(idStory)
                                .orElseThrow(() -> new AppException(ErrorCode.INVALID_STORY));
                Pageable pageable = PaginationUtil.createPageable(req.getPage(), req.getLimit());
                User user = s.getAuthor();
                Page<Story> storyPage = storyRepository.findAllByAuthor(user.getId(), pageable);
                List<StoriesResponse> stories = storyPage.getContent().stream().map(this::convertToResponse).toList();
                return PageResponse.<StoriesResponse>builder()
                                .data(stories)
                                .page(req.getPage())
                                .limit(req.getLimit())
                                .total(storyPage.getTotalPages())
                                .build();
        }

        @Override
        public void toggleVisibilityStory(Boolean isVisibility, String id) {
                User user = getAuthenticatedUser();
                Story story = storyRepository.findByIdAndAuthor(id, user.getId())
                                .orElseThrow(() -> new AppException(ErrorCode.INVALID_STORY));
                story.setVisibility(isVisibility);
                storyRepository.save(story);
        }

        @Override
        public void updateStoryBanStatus(String id, boolean isBan) {
                Story story = storyRepository.findById(id)
                                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND, "Story"));
                
                story.setBanned(isBan);
                storyRepository.save(story);
        }

}
