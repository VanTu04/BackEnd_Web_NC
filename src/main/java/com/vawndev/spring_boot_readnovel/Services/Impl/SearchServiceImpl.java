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
import com.vawndev.spring_boot_readnovel.Utils.PaginationUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import static com.vawndev.spring_boot_readnovel.Specification.StorySpecification.searchByKeyword;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SearchServiceImpl implements SearchService {
    private final SearchRepository searchRepository;
    private final StoryMapper storyMapper;
    @Override
    public PageResponse<StoriesResponse> searchStory(String keyword, PageRequest req) {
        if (keyword == null || keyword.isEmpty()) {
            throw new AppException(ErrorCode.INVALID_STORY);
        }
        Pageable pageable = PaginationUtil.createPageable(req.getPage(), req.getLimit());
        Specification<Story> spec=searchByKeyword(keyword);
        Page<Story> storyPage=searchRepository.findAll(spec,pageable);
        List<StoriesResponse> storiesList = storyPage.getContent().stream().map(stry->storyMapper.toStoriesResponse(stry)).collect(Collectors.toList());
        return PageResponse.<StoriesResponse>builder().data(storiesList).page(req.getPage()).limit(req.getLimit()).limit(req.getLimit()).build();
    }
}
