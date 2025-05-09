package com.vawndev.spring_boot_readnovel.Controllers;

import com.vawndev.spring_boot_readnovel.Dto.Responses.ApiResponse;
import com.vawndev.spring_boot_readnovel.Dto.Responses.Report.StatisticsResponse;
import com.vawndev.spring_boot_readnovel.Services.ReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/admin/statistics")
@RequiredArgsConstructor
@Slf4j
public class AdminReportController {

    private final ReportService reportService;

    @GetMapping
    public ApiResponse<List<StatisticsResponse>> getStatistics(
            @RequestParam String filter,
            @RequestParam Integer selectedYear) {

        List<StatisticsResponse> response = reportService.getStatistics(filter, selectedYear);
        return ApiResponse.<List<StatisticsResponse>>builder()
                .result(response)
                .build();
    }
}
