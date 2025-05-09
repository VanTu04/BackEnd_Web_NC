package com.vawndev.spring_boot_readnovel.Services.Impl;

import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders;
import com.vawndev.spring_boot_readnovel.Dto.Responses.Category.CategoryResponse;
import com.vawndev.spring_boot_readnovel.Dto.Responses.PageResponse;
import com.vawndev.spring_boot_readnovel.Dto.Responses.Story.StoriesResponse;
import com.vawndev.spring_boot_readnovel.Entities.Story;
import com.vawndev.spring_boot_readnovel.Entities.StoryDocument;
import com.vawndev.spring_boot_readnovel.Enum.IS_AVAILBLE;
import com.vawndev.spring_boot_readnovel.Enum.STORY_STATUS;
import com.vawndev.spring_boot_readnovel.Exceptions.AppException;
import com.vawndev.spring_boot_readnovel.Mappers.StoryMapper;
import com.vawndev.spring_boot_readnovel.Repositories.SearchRepository;
import com.vawndev.spring_boot_readnovel.Repositories.StoryRepository;
import com.vawndev.spring_boot_readnovel.Services.SearchService;
import com.vawndev.spring_boot_readnovel.Specification.StorySpecification;
import com.vawndev.spring_boot_readnovel.Utils.PaginationUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;


import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders.*;

@Service
@RequiredArgsConstructor
public class SearchServiceImpl implements SearchService {
    private final SearchRepository searchRepository;
    private final StoryMapper storyMapper;
    private final StoryRepository storyRepository;
    private final ElasticsearchTemplate elasticsearchTemplate;

    @Override
    public PageResponse<StoriesResponse> searchStory(int page, int limit,
            Map<String, String> filterFields) {
        try {
            Pageable pageable = PaginationUtil.createPageable(page, limit);

            Specification<Story> spec = StorySpecification.searchAndFilter(filterFields);

            Page<Story> storyPage = searchRepository.findAll(spec, pageable);

            List<StoriesResponse> storiesList = storyPage.getContent().stream()
                    .map(storyMapper::toStoriesResponse)
                    .collect(Collectors.toList());

            // Trả về kết quả phân trang
            return PageResponse.<StoriesResponse>builder()
                    .data(storiesList)
                    .page(page)
                    .limit(limit)
                    .total(storyPage.getTotalPages())
                    .build();

        } catch (AppException e) {
            throw e; // Nếu là AppException, ném lại để giữ nguyên mã lỗi
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi tìm kiếm truyện: " + e.getMessage(), e);
        }
    }

    public PageResponse<StoriesResponse> cateStory(String keyword, int page, int limit) {
        Pageable pageable = PaginationUtil.createPageable(page, limit);

        Page<Story> storyPage;

        if (!"all".equalsIgnoreCase(keyword)) {
            storyPage = storyRepository.findAcceptedByCate(keyword, pageable);
        } else {
            List<STORY_STATUS> status = List.of(STORY_STATUS.COMPLETED, STORY_STATUS.UPDATING);
            storyPage = storyRepository.findAccepted(IS_AVAILBLE.ACCEPTED, status, pageable);
        }

        List<StoriesResponse> storiesList = storyPage.getContent().stream()
                .map(storyMapper::toStoriesResponse)
                .collect(Collectors.toList());

        return PageResponse.<StoriesResponse>builder()
                .data(storiesList)
                .page(page)
                .limit(limit)
                .total(storyPage.getTotalPages())
                .build();
    }

    @Override
    public PageResponse<StoriesResponse> elasticSearchStory(String keyword, int page, int limit) {
            var criteria = QueryBuilders.bool(builder -> builder.should(
                    // Tìm kiếm gần đúng (Fuzzy Search)
                    fuzzy(queryTitle -> queryTitle.field("title").value(keyword).fuzziness("AUTO")),
                    fuzzy(queryAuthor -> queryAuthor.field("authorName").value(keyword).fuzziness("AUTO")),
                    fuzzy(queryDesc -> queryDesc.field("description").value(keyword).fuzziness("AUTO")),
                    fuzzy(queryCategory -> queryCategory.field("categories").value(keyword).fuzziness("AUTO")),

                    // Tìm kiếm mù (Wildcard Search)
                    wildcard(queryTitleWildcard -> queryTitleWildcard.field("title").value("*" + keyword + "*")),
                    wildcard(queryAuthorWildcard -> queryAuthorWildcard.field("authorName").value("*" + keyword + "*")),
                    wildcard(queryCategoryWildcard -> queryCategoryWildcard.field("categories").value("*" + keyword + "*"))
            ).filter(
                    term(queryAvailable -> queryAvailable.field("isAvailable").value(IS_AVAILBLE.ACCEPTED.name())),
                    term(queryVisibility -> queryVisibility.field("isVisibility").value(true)),
                    term(queryIdBanned -> queryIdBanned.field("isBanned").value(false))
            ));

            // Tạo truy vấn tìm kiếm với phân trang
            var searchQuery = NativeQuery.builder()
                    .withQuery(criteria)
                    .withPageable(PageRequest.of(page, limit))
                    .build();

        var searchResult = elasticsearchTemplate.search(searchQuery, StoryDocument.class);

        List<StoriesResponse> stories = toStoriesResponse(
                searchResult.stream().map(SearchHit::getContent).toList());

        long totalElements = searchResult.getTotalHits();

            return PageResponse.<StoriesResponse>builder()
                    .data(stories)
                    .page(page)
                    .limit(limit)
                    .total((int)totalElements)
                    .build();
    }

    private List<StoriesResponse> toStoriesResponse(List<StoryDocument> storyDocument) {
        return storyDocument.stream().map(story -> StoriesResponse.builder()
                .id(story.getId())
                .title(story.getTitle())
                .type(story.getType())
                .email(story.getAuthorEmail())
                .view(story.getViews())
                .isVisibility(story.getIsVisibility())
                .isBanned(story.getIsBanned())
                .isAvailble(IS_AVAILBLE.valueOf(story.getIsAvailable()))
                .categories(convertCategories(story.getCategories()))
                .build()).collect(Collectors.toList());
    }

    private List<CategoryResponse> convertCategories(List<String> categories) {
        if (categories == null || categories.isEmpty()) {
            return Collections.emptyList();
        }

        return categories.stream().map(categoryName -> {
            CategoryResponse category = new CategoryResponse();
            category.setName(categoryName);
            return category;
        }).toList();
    }
}