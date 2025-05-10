package com.vawndev.spring_boot_readnovel.Dto.Responses;

import java.math.BigDecimal;

import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PurchaseHistoryDTORes {
    private String stories_id;
    private String cover;
    private String storyTitle;
    private String chapter_id;
    private String chapterTitle;
    private BigDecimal balance;
    private BigDecimal price;
    private String created;
}
