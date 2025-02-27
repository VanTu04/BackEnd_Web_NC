package com.vawndev.spring_boot_readnovel.Dto.Requests;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StoryRequest {
    private String title;
    private String description;
    private String type;
    private BigDecimal price;
    private boolean isAvailable;
    private String state;
    private boolean isApproved;
    private double rate;
    private int views;
    private List<String> categoryIds;
    private String coverImage;
}