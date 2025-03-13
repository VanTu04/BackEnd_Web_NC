package com.vawndev.spring_boot_readnovel.Dto.Requests.Story;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.vawndev.spring_boot_readnovel.Dto.Requests.Category.CategoryRequests;
import com.vawndev.spring_boot_readnovel.Entities.Category;
import com.vawndev.spring_boot_readnovel.Enum.StoryType;
import lombok.*;
import net.minidev.json.annotate.JsonIgnore;

import java.math.BigDecimal;
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

    private String emailAuthor;

    private List<CategoryRequests> categories;

}
