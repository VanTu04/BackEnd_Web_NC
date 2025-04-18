package com.vawndev.spring_boot_readnovel.Dto.Requests.Story;

import com.vawndev.spring_boot_readnovel.Dto.Requests.Category.CategoryRequests;
import com.vawndev.spring_boot_readnovel.Enum.StoryType;
import lombok.*;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder

public class StoryRequests {

    private String title;

    private String description;

    private StoryType type;

    private List<CategoryRequests> categories;


}
