package com.vawndev.spring_boot_readnovel.Dto.Requests.Story;

import com.vawndev.spring_boot_readnovel.Entities.Category;
import com.vawndev.spring_boot_readnovel.Entities.User;
import com.vawndev.spring_boot_readnovel.Enum.StoryState;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class StoryRequests {
    private String id;

    private String title;

    private String description;

    private String type;

    private String emailAuthor;

    private BigDecimal price;

    private boolean isAvailable;

    private StoryState state;

    private boolean isApproved;

    private List<Category> categories;

    private LocalDateTime updatedAt;

    private LocalDateTime deletedAt;

    private MultipartFile coverImage;
}
