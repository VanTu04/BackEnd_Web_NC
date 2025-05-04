package com.vawndev.spring_boot_readnovel.Dto.Responses.Chapter;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class ChaptersResponse {
    private String id;

    private String title;

    private String content;

    private Long views;

    private String prev;

    private String next;

    private String createdAt;

    private boolean isRead;
}
