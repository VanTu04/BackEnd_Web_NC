package com.vawndev.spring_boot_readnovel.Dto.Requests.Story;

import com.vawndev.spring_boot_readnovel.Dto.Requests.Category.CategoryRequests;
import com.vawndev.spring_boot_readnovel.Entities.Category;
import com.vawndev.spring_boot_readnovel.Enum.StoryType;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Data
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

    private BigDecimal price=BigDecimal.ZERO;

    private List<CategoryRequests> categories;

}
