package com.vawndev.spring_boot_readnovel.Dto.Requests.Chapter;

import com.vawndev.spring_boot_readnovel.Entities.Image;
import com.vawndev.spring_boot_readnovel.Entities.Story;
import lombok.*;


import java.math.BigDecimal;
import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
public class ChapterRequest {
    private String id;

    private String title;

    private String story_id;

    private String content;

    private BigDecimal price;

    private List<Image> images;
}
