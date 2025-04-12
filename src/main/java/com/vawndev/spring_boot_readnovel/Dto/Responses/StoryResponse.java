package com.vawndev.spring_boot_readnovel.Dto.Responses;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Locale.Category;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StoryResponse {
    private String id;
    private String title;
    private String description;
    private String type;
    private BigDecimal price;
    private boolean isAvailable;
    private String state;
    private boolean isApproved;
    private double rate;
    private int views;
    private List<Category> categories;
    private String coverImage;
    private Date publishDate;
    private Date updateDate;
}