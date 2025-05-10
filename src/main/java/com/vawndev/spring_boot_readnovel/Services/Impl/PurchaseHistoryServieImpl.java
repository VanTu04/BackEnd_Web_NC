package com.vawndev.spring_boot_readnovel.Services.Impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Sort;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.vawndev.spring_boot_readnovel.Dto.Responses.PageResponse;
import com.vawndev.spring_boot_readnovel.Dto.Responses.PurchaseHistoryDTORes;
import com.vawndev.spring_boot_readnovel.Entities.PurchaseHistory;
import com.vawndev.spring_boot_readnovel.Entities.User;
import com.vawndev.spring_boot_readnovel.Repositories.PurChaseRepository;
import com.vawndev.spring_boot_readnovel.Services.PurchaseHistoryService;
import com.vawndev.spring_boot_readnovel.Utils.TimeZoneConvert;
import com.vawndev.spring_boot_readnovel.Utils.Help.TokenHelper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor

public class PurchaseHistoryServieImpl implements PurchaseHistoryService {
    private final TokenHelper tokenHelper;
    private final PurChaseRepository chaseRepository;

    @Override
    public PageResponse<PurchaseHistoryDTORes> getAll(int page, int limit) {
        User user = tokenHelper.getUserO2Auth();

        Pageable pageable = PageRequest.of(page, limit, Sort.by("createdAt").descending());

        List<PurchaseHistory> historyList = chaseRepository.getAllByUserId(user.getId(), pageable);

        List<PurchaseHistoryDTORes> dtoList = historyList.stream()
                .map(p -> PurchaseHistoryDTORes.builder().balance(p.getBalance())
                        .chapterTitle(p.getChapter().getTitle())
                        .chapter_id(p.getChapter().getId())
                        .stories_id(p.getChapter().getStory().getId())
                        .storyTitle(p.getChapter().getStory().getTitle())
                        .cover(p.getChapter().getStory().getCoverImage())
                        .price(p.getPrice())
                        .created(TimeZoneConvert.convertUtcToUserTimezone(p.getCreatedAt()))
                        .build()) // giả sử bạn dùng ModelMapper
                .collect(Collectors.toList());

        // Trả về PageResponse
        return PageResponse.<PurchaseHistoryDTORes>builder().data(dtoList).page(page).limit(limit)
                .total(pageable.getPageNumber()).build();
    }
}
