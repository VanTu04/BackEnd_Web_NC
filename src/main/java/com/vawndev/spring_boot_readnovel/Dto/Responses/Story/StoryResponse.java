package com.vawndev.spring_boot_readnovel.Dto.Responses.Story;

import com.vawndev.spring_boot_readnovel.Dto.Responses.Category.CategoriesResponse;
import com.vawndev.spring_boot_readnovel.Dto.Responses.Category.CategoryResponse;
import com.vawndev.spring_boot_readnovel.Dto.Responses.User.UserResponse;
import com.vawndev.spring_boot_readnovel.Entities.Category;
import com.vawndev.spring_boot_readnovel.Entities.User;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
@SuperBuilder
public class StoryResponse {
    private String title;

    private String description;

    private Long view;

    private String type;

    private UserResponse author;

    private BigDecimal price;

    private boolean isAvailable;

    private double rate;

    private int views;

    private List<CategoryResponse> categories;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private String coverImage;

}
