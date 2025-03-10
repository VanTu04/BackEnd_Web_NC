package com.vawndev.spring_boot_readnovel.Dto.Responses.Story;

import com.vawndev.spring_boot_readnovel.Entities.Category;
import com.vawndev.spring_boot_readnovel.Entities.User;
import lombok.*;

import java.math.BigDecimal;
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

    private String type;

    private User author;

    private BigDecimal price;

    private boolean isAvailable;


    private double rate;

    private int views;

    private List<Category> categories;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private String coverImage;

}
