package com.vawndev.spring_boot_readnovel.Dto.Requests;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChapterRequest {
    private String title;
    private String content;
    private BigDecimal price;
}