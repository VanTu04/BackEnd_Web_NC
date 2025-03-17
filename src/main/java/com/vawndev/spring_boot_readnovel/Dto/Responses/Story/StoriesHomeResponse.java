package com.vawndev.spring_boot_readnovel.Dto.Responses.Story;

import com.vawndev.spring_boot_readnovel.Dto.Responses.Category.CategoriesResponse;
import com.vawndev.spring_boot_readnovel.Dto.Responses.PageResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StoriesHomeResponse {
    private CategoriesResponse categories;
    private PageResponse<StoriesResponse> stories;
    private PageResponse<StoriesResponse> getStoriesComingSoon;
    private PageResponse<StoriesResponse> getStoriesUpdating;
    private List<StoriesResponse> getStoriesRank;
}
