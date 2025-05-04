package com.vawndev.spring_boot_readnovel.Services;

import com.vawndev.spring_boot_readnovel.Dto.Responses.Report.StatisticsResponse;

import java.util.List;

public interface ReportService {

    List<StatisticsResponse> getStatistics(String filter, Integer selectedYear);
}
