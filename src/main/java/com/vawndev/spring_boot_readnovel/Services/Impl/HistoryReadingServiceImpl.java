package com.vawndev.spring_boot_readnovel.Services.Impl;

import com.vawndev.spring_boot_readnovel.Dto.Requests.ConditionRequest;
import com.vawndev.spring_boot_readnovel.Dto.Requests.PageRequest;
import com.vawndev.spring_boot_readnovel.Dto.Responses.Chapter.ChapterResponsePurchase;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HistoryReadingServiceImpl implements HistoryReadingService {
    private final StoryRepository storyRepository;
    private final ChapterRepository chapterRepository;
    private final ReadingHistoryRepository readingHistoryRepository;
    private final JwtUtils jwtUtils;
    private final TokenHelper tokenHelper;

    @Override
    public PageResponse<ReadingHistoryResponse> getHistory(String bearerToken, PageRequest req) {
        User user = jwtUtils.validToken(tokenHelper.getTokenInfo(bearerToken));
        Pageable pageable = PaginationUtil.createPageable(req.getPage(), req.getLimit());

        Page<ReadingHistory> histories = readingHistoryRepository.findByUser(user.getId(), pageable);

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
    public void saveHistory(String bearerToken, String chapter_id) {
        User user = jwtUtils.validToken(tokenHelper.getTokenInfo(bearerToken));
        Chapter chapter=chapterRepository.findById(chapter_id).orElseThrow(()-> new AppException(ErrorCode.NOT_FOUND));
        ReadingHistory readingHistory=readingHistoryRepository.findByUserAndChapter(user, chapter);
        if (Objects.nonNull(readingHistory)) {
            return;
        }
        chapter.getStory().setViews(chapter.getStory().getViews() + 1);
        readingHistory = ReadingHistory
                .builder()
                .chapter(chapter)
                .user(user)
                .build();
        readingHistoryRepository.save(readingHistory);
    }

    @Override
    public void deleteHistory(String bearerToken, String story_id) {
        User user = jwtUtils.validToken(tokenHelper.getTokenInfo(bearerToken));

        Story story=storyRepository.findById(story_id).orElseThrow(()-> new AppException(ErrorCode.NOT_FOUND));
        ReadingHistory readingHistory=readingHistoryRepository.findByUserAndStory(user,story);
        readingHistoryRepository.delete(readingHistory);
    }

    @Override
    public void deleteAllHistory(String bearerToken) {
        User user = jwtUtils.validToken(tokenHelper.getTokenInfo(bearerToken));
        readingHistoryRepository.deleteByUserId(user.getId());
    }


}
