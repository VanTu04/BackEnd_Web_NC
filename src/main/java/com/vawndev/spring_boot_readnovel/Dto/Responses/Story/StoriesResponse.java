package com.vawndev.spring_boot_readnovel.Dto.Responses.Story;


import com.vawndev.spring_boot_readnovel.Dto.Responses.Category.CategoriesResponse;
import com.vawndev.spring_boot_readnovel.Dto.Responses.Category.CategoryResponse;
import com.vawndev.spring_boot_readnovel.Dto.Responses.TimeZoneResponse;
import com.vawndev.spring_boot_readnovel.Enum.STORY_STATUS;
import com.vawndev.spring_boot_readnovel.Enum.StoryType;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class StoriesResponse extends TimeZoneResponse {

    private String id;

    private String title;

    private StoryType type;

    private Long view;

    private int views;

    private STORY_STATUS status;

    private List<CategoryResponse> categories;

    private String coverImage;

}
