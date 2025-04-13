package com.vawndev.spring_boot_readnovel.Services.Impl;

import com.vawndev.spring_boot_readnovel.Dto.Requests.PageRequest;
import com.vawndev.spring_boot_readnovel.Dto.Responses.PageResponse;
import com.vawndev.spring_boot_readnovel.Dto.Responses.Story.StoriesResponse;
import com.vawndev.spring_boot_readnovel.Dto.Responses.Story.StoryResponse;
import com.vawndev.spring_boot_readnovel.Entities.Story;
import com.vawndev.spring_boot_readnovel.Exceptions.AppException;
import com.vawndev.spring_boot_readnovel.Exceptions.ErrorCode;
import com.vawndev.spring_boot_readnovel.Mappers.StoryMapper;
import com.vawndev.spring_boot_readnovel.Repositories.SearchRepository;
import com.vawndev.spring_boot_readnovel.Services.SearchService;
import com.vawndev.spring_boot_readnovel.Specification.StorySpecification;
import com.vawndev.spring_boot_readnovel.Utils.PaginationUtil;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SearchServiceImpl implements SearchService {
    private final SearchRepository searchRepository;
    private final StoryMapper storyMapper;
    @Override
    public PageResponse<StoriesResponse> searchStory(String keyword, int page, int limit, Set<String> filterFields) {
        try {
            Pageable pageable = PaginationUtil.createPageable(page, limit);

            // Gọi Specification với keyword và các trường lọc
            Specification<Story> spec = StorySpecification.searchAndFilter(keyword, filterFields);

            Page<Story> storyPage = searchRepository.findAll(spec, pageable);

            List<StoriesResponse> storiesList = storyPage.getContent().stream()
                    .map(storyMapper::toStoriesResponse)
                    .collect(Collectors.toList());

            // Trả về kết quả phân trang
            return PageResponse.<StoriesResponse>builder()
                    .data(storiesList)
                    .page(page)
                    .limit(limit)
                    .total(storyPage.getTotalPages()) // Sửa lại để lấy tổng số bản ghi
                    .build();

        } catch (AppException e) {
            throw e; // Nếu là AppException, ném lại để giữ nguyên mã lỗi
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi tìm kiếm truyện: " + e.getMessage(), e);
        }
    }

}
