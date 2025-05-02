package com.vawndev.spring_boot_readnovel.Dto.Responses.Report;

import lombok.Data;

@Data
public class StatisticsResponse {
    private String periodLabel; // ví dụ: "2025-04", "Q1-2025", "2025"
    private long newUsers;
    private long newStories;
    private long transactionCount;
}
