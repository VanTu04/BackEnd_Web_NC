package com.vawndev.spring_boot_readnovel.Controllers;

import com.vawndev.spring_boot_readnovel.Dto.Responses.ApiResponse;
import com.vawndev.spring_boot_readnovel.Services.ReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/admin/report")
@RequiredArgsConstructor
@Slf4j
public class AdminReportController {

    private final ReportService reportService;

    @GetMapping("/base")
    public ApiResponse<?> getReportByAdmin(@RequestParam(value = "year", required = false) Integer year,
                                           @RequestParam(value = "month", required = false) Integer month){

        return ApiResponse.<Map<String, Object>>builder()
                .result(reportService.statistics(year, month))
                .build();
    }
}
