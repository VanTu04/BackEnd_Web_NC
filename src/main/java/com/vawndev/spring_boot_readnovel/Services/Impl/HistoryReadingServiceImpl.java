package com.vawndev.spring_boot_readnovel.Services.Impl;

import com.vawndev.spring_boot_readnovel.Dto.Requests.PageRequest;
import com.vawndev.spring_boot_readnovel.Dto.Responses.Chapter.ChaptersResponse;
import com.vawndev.spring_boot_readnovel.Dto.Responses.My.ReadingHistoryResponse;
import com.vawndev.spring_boot_readnovel.Dto.Responses.PageResponse;
import com.vawndev.spring_boot_readnovel.Dto.Responses.Story.StoriesResponse;
import com.vawndev.spring_boot_readnovel.Entities.Chapter;
import com.vawndev.spring_boot_readnovel.Entities.ReadingHistory;
import com.vawndev.spring_boot_readnovel.Entities.Story;
import com.vawndev.spring_boot_readnovel.Entities.User;
import com.vawndev.spring_boot_readnovel.Enum.IS_AVAILBLE;
import com.vawndev.spring_boot_readnovel.Enum.STORY_STATUS;
import com.vawndev.spring_boot_readnovel.Exceptions.AppException;
import com.vawndev.spring_boot_readnovel.Exceptions.ErrorCode;
import com.vawndev.spring_boot_readnovel.Repositories.ChapterRepository;
import com.vawndev.spring_boot_readnovel.Repositories.ReadingHistoryRepository;
import com.vawndev.spring_boot_readnovel.Repositories.StoryRepository;
import com.vawndev.spring_boot_readnovel.Services.HistoryReadingService;
import com.vawndev.spring_boot_readnovel.Utils.Help.TokenHelper;
import com.vawndev.spring_boot_readnovel.Utils.PaginationUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HistoryReadingServiceImpl implements HistoryReadingService {
    private final StoryRepository storyRepository;
    private final ChapterRepository chapterRepository;
    private final ReadingHistoryRepository readingHistoryRepository;
    private final TokenHelper tokenHelper;

    private User getAuthenticatedUser() {
        return tokenHelper.getUserO2Auth();
    }

    @Override
    public PageResponse<ReadingHistoryResponse> getHistory(PageRequest req) {
        User user = getAuthenticatedUser();

        Pageable pageable = PaginationUtil.createPageable(req.getPage(), req.getLimit());

        Page<ReadingHistory> histories = readingHistoryRepository.findByUser(user, pageable);

        // Nhóm chapter theo story_id
        Map<String, Set<String>> storyChapterMap = histories.getContent()
                .stream()
                .collect(Collectors.groupingBy(
                        history -> history.getChapter().getStory().getId(),
                        Collectors.mapping(history -> history.getChapter().getId(), Collectors.toSet())));

        List<ReadingHistoryResponse> result = new ArrayList<>();
        storyChapterMap.forEach((storyId, chapterIds) -> {
            // Tìm story từ storyId
            Story story = storyRepository.findById(storyId).orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND));
            if (story != null) {
                StoriesResponse storyResponse = StoriesResponse
                        .builder()
                        .id(story.getId())
                        .title(story.getTitle())
                        .status(story.getStatus())
                        .coverImage(story.getCoverImage())
                        .build();

                List<ChaptersResponse> chapterResponses = chapterIds.stream().map(
                        chapterId -> {
                            Chapter chapter = chapterRepository.findById(chapterId)
                                    .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND));
                            return ChaptersResponse
                                    .builder()
                                    .id(chapter.getId())
                                    .title(chapter.getTitle())
                                    .content(chapter.getContent())
                                    .build();
                        }).collect(Collectors.toList());

                result.add(new ReadingHistoryResponse(storyResponse, chapterResponses));
            }
        });

        return PageResponse.<ReadingHistoryResponse>builder()
                .limit(req.getLimit())
                .page(req.getPage())
                .data(result)
                .total(histories.getTotalPages())
                .build();
    }

    @Override
    public Set<String> getChaptersIdHistory(String storyId, User currentUser) {
        if (currentUser == null) {
            return Collections.emptySet();
        }
        storyRepository.findByAcceptId(storyId)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND, "Story history not found"));
        List<Chapter> chapter = readingHistoryRepository
                .findReadingChapters(storyId, currentUser.getId());

        Set<String> chapterIds = chapter.stream().map(Chapter::getId).collect(Collectors.toSet());
        return chapterIds;

    }

    @Override
    public void saveHistory(String chapter_id) {
        User user = getAuthenticatedUser();
        Chapter chapter = chapterRepository.findById(chapter_id)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND));
        ReadingHistory readingHistory = readingHistoryRepository.findByUserAndChapter(user, chapter);
        if (Objects.nonNull(readingHistory)) {
            return;
        }
        chapter.getStory().setViews(chapter.getStory().getViews() + 1);
        chapter.setViews(chapter.getViews() + 1);
        readingHistory = ReadingHistory
                .builder()
                .chapter(chapter)
                .user(user)
                .build();
        readingHistoryRepository.save(readingHistory);
    }

    @Override
    public void deleteHistory(String story_id) {
        User user = getAuthenticatedUser();

        Story story = storyRepository.findById(story_id).orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND));
        ReadingHistory readingHistory = readingHistoryRepository.findByUserAndStory(user, story);
        readingHistoryRepository.delete(readingHistory);
    }

    @Override
    public void deleteAllHistory() {
        User user = getAuthenticatedUser();
        readingHistoryRepository.deleteByUserId(user.getId());
    }

    public ChaptersResponse getLatestChapter(String storyId) {
        User user = getAuthenticatedUser();
        List<STORY_STATUS> statusList = List.of(STORY_STATUS.COMPLETED, STORY_STATUS.UPDATING);
        storyRepository.findAcceptedId(IS_AVAILBLE.ACCEPTED, statusList, storyId)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND, "Story"));
        Chapter chapter = readingHistoryRepository
                .findLatestChapter(storyId, user.getId())
                .orElseGet(() -> readingHistoryRepository
                        .findFirstChapterByStoryId(storyId)
                        .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND, "Story not found")));

        return ChaptersResponse.builder()
                .content(chapter.getContent())
                .title(chapter.getTitle())
                .views(chapter.getViews())
                .id(chapter.getId())
                .build();
    }

}
