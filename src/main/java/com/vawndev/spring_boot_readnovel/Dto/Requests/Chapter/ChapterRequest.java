package com.vawndev.spring_boot_readnovel.Dto.Requests.Chapter;

import lombok.*;


import java.math.BigDecimal;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
public class ChapterRequest {

    private String title;

    private String authorEmail;

    private String story_id;

    private String content;

    private BigDecimal price;

}
