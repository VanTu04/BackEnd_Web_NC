package com.vawndev.spring_boot_readnovel.Services.Impl;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.vawndev.spring_boot_readnovel.Dto.Requests.PageRequest;
import com.vawndev.spring_boot_readnovel.Dto.Responses.Chapter.ChaptersResponse;
import com.vawndev.spring_boot_readnovel.Dto.Responses.My.ReadingHistoryResponse;
import com.vawndev.spring_boot_readnovel.Dto.Responses.PageResponse;
import com.vawndev.spring_boot_readnovel.Dto.Responses.Story.StoriesResponse;
import com.vawndev.spring_boot_readnovel.Entities.Chapter;
import com.vawndev.spring_boot_readnovel.Entities.ReadingHistory;
import com.vawndev.spring_boot_readnovel.Entities.Story;
import com.vawndev.spring_boot_readnovel.Entities.User;
import com.vawndev.spring_boot_readnovel.Exceptions.AppException;
import com.vawndev.spring_boot_readnovel.Exceptions.ErrorCode;
import com.vawndev.spring_boot_readnovel.Repositories.ChapterRepository;
import com.vawndev.spring_boot_readnovel.Repositories.ReadingHistoryRepository;
import com.vawndev.spring_boot_readnovel.Repositories.StoryRepository;
import com.vawndev.spring_boot_readnovel.Services.HistoryReadingService;
import com.vawndev.spring_boot_readnovel.Utils.Help.TokenHelper;
import com.vawndev.spring_boot_readnovel.Utils.JwtUtils;
import com.vawndev.spring_boot_readnovel.Utils.PaginationUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class HistoryReadingServiceImpl implements HistoryReadingService {
    private final StoryRepository storyRepository;
    private final ChapterRepository chapterRepository;
    private final ReadingHistoryRepository readingHistoryRepository;
    private final JwtUtils jwtUtils;
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
                        Collectors.mapping(history -> history.getChapter().getId(), Collectors.toSet())
                ));

        List<ReadingHistoryResponse> result = new ArrayList<>();
        storyChapterMap.forEach((storyId, chapterIds) -> {
            // Tìm story từ storyId
            Story story = storyRepository.findById(storyId).orElseThrow(()-> new AppException(ErrorCode.NOT_FOUND));
            if (story != null) {
                StoriesResponse storyResponse =StoriesResponse
                        .builder()
                        .id(story.getId())
                        .title(story.getTitle())
                        .status(story.getStatus())
                        .coverImage(story.getCoverImage())
                        .build();

                List<ChaptersResponse> chapterResponses = chapterIds.stream().map(
                        chapterId->{
                            Chapter chapter = chapterRepository.findById(chapterId).orElseThrow(()-> new AppException(ErrorCode.NOT_FOUND));
                            return ChaptersResponse
                                    .builder()
                                    .id(chapter.getId())
                                    .title(chapter.getTitle())
                                    .content(chapter.getContent())
                                    .build();
                        }
                ).collect(Collectors.toList());

                result.add(new ReadingHistoryResponse(storyResponse, chapterResponses));
            }
        });

        return  PageResponse.<ReadingHistoryResponse>builder()
                .limit(req.getLimit())
                .page(req.getPage())
                .data(result)
                .total(histories.getTotalPages())
                .build();
    }

    @Override
    public void saveHistory( String chapter_id) {
        User user = getAuthenticatedUser();
        Chapter chapter=chapterRepository.findById(chapter_id).orElseThrow(()-> new AppException(ErrorCode.NOT_FOUND));
        ReadingHistory readingHistory=readingHistoryRepository.findByUserAndChapter(user, chapter);
        if (Objects.nonNull(readingHistory)) {
            return;
        }
        chapter.getStory().setViews(chapter.getStory().getViews() + 1);
        chapter.setViews(chapter.getViews() + 1);
        chapter.setCreatedAt(Instant.now());
        readingHistory = ReadingHistory
                .builder()
                .chapter(chapter)
                .user(user)
                .build();
        readingHistoryRepository.save(readingHistory);
    }

    @Override
    public void deleteHistory( String story_id) {
        User user = getAuthenticatedUser();

        Story story=storyRepository.findById(story_id).orElseThrow(()-> new AppException(ErrorCode.NOT_FOUND));
        ReadingHistory readingHistory=readingHistoryRepository.findByUserAndStory(user,story);
        readingHistoryRepository.delete(readingHistory);
    }

    @Override
    public void deleteAllHistory() {
        User user = getAuthenticatedUser();
        readingHistoryRepository.deleteByUserId(user.getId());
    }

    @Override
    public ReadingHistoryResponse getLatestHistory() {
        User user = getAuthenticatedUser(); // Lấy thông tin người dùng hiện tại

        // Lấy lịch sử đọc gần nhất
        ReadingHistory latestHistory = readingHistoryRepository.findFirstByUserIdOrderByCreatedAtDesc(user.getId())
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND, "No reading history found for the user."));

        // Lấy thông tin truyện và chương từ lịch sử đọc
        Story story = latestHistory.getChapter().getStory();
        StoriesResponse storyResponse = StoriesResponse.builder()
                .id(story.getId())
                .title(story.getTitle())
                .status(story.getStatus())
                .coverImage(story.getCoverImage())
                .build();

        Chapter chapter = latestHistory.getChapter();
        ChaptersResponse chapterResponse = ChaptersResponse.builder()
                .id(chapter.getId())
                .title(chapter.getTitle())
                .content(chapter.getContent())
                .build();

        return new ReadingHistoryResponse(storyResponse, List.of(chapterResponse));
    }
}
