package com.vawndev.spring_boot_readnovel.Dto.Responses.Story;

import com.vawndev.spring_boot_readnovel.Entities.Category;
import com.vawndev.spring_boot_readnovel.Entities.User;
import com.vawndev.spring_boot_readnovel.Enum.IS_AVAILBLE;
import com.vawndev.spring_boot_readnovel.Enum.STORY_STATUS;
import com.vawndev.spring_boot_readnovel.Enum.StoryType;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
@Builder
public class StoryResponse {
    private String title;

    private String description;

    private Long view;

    private StoryType type;

    private User author;

    private BigDecimal price;

    private IS_AVAILBLE isAvailable;

    private STORY_STATUS status;

    private double rate;

    private int views;

    private List<Category> categories;

    private Instant createdAt;

    private Instant updatedAt;

    private String coverImage;

}
