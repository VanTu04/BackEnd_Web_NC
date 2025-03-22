package com.vawndev.spring_boot_readnovel.Dto.Responses;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChapterResponse {
    private String id;
    private String title;
    private String content;
    private BigDecimal price;
    private Date publishDate;
    private Date updateDate;
}