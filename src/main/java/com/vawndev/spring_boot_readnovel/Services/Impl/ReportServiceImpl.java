package com.vawndev.spring_boot_readnovel.Services.Impl;

import com.vawndev.spring_boot_readnovel.Constants.PredefinedRole;
import com.vawndev.spring_boot_readnovel.Dto.Responses.Report.StatisticsResponse;
import com.vawndev.spring_boot_readnovel.Repositories.StoryRepository;
import com.vawndev.spring_boot_readnovel.Repositories.UserRepository;
import com.vawndev.spring_boot_readnovel.Repositories.WalletTransactionRepository;
import com.vawndev.spring_boot_readnovel.Services.ReportService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final StoryRepository storyRepository;
    private final UserRepository userRepository;
    private final WalletTransactionRepository walletTransactionRepository;

    @Override
    public List<StatisticsResponse> getStatistics(String filter, Integer selectedYear) {
        List<StatisticsResponse> responses = new ArrayList<>();

        int currentYear = Year.now(ZoneOffset.UTC).getValue();

        switch (filter.toLowerCase()) {
            case "month" -> {
                // selectedYear phải được cung cấp khi filter là "month"
                if (selectedYear == null) {
                    throw new IllegalArgumentException("selectedYear is required for month filter");
                }

                // Lặp qua các tháng trong năm selectedYear
                for (int month = 1; month <= 12; month++) {
                    Instant periodStart = LocalDate.of(selectedYear, month, 1)
                            .atStartOfDay(ZoneOffset.UTC).toInstant();
                    Instant periodEnd = LocalDate.of(selectedYear, month, Month.of(month).length(Year.isLeap(selectedYear)))
                            .atTime(23, 59, 59).atZone(ZoneOffset.UTC).toInstant();

                    String label = String.format("%04d-%02d", selectedYear, month);
                    responses.add(getStatisticsResponse(periodStart, periodEnd, label));
                }
            }
            case "quarter" -> {
                // selectedYear phải được cung cấp khi filter là "quarter"
                if (selectedYear == null) {
                    throw new IllegalArgumentException("selectedYear is required for quarter filter");
                }

                // Lặp qua các quý trong năm selectedYear
                for (int quarter = 1; quarter <= 4; quarter++) {
                    int startMonth = (quarter - 1) * 3 + 1;
                    LocalDate quarterStart = LocalDate.of(selectedYear, startMonth, 1);
                    LocalDate quarterEnd = quarterStart.plusMonths(2).withDayOfMonth(quarterStart.plusMonths(2).lengthOfMonth());

                    Instant periodStart = quarterStart.atStartOfDay(ZoneOffset.UTC).toInstant();
                    Instant periodEnd = quarterEnd.atTime(23, 59, 59).atZone(ZoneOffset.UTC).toInstant();

                    String label = "Q" + quarter + "-" + selectedYear;
                    responses.add(getStatisticsResponse(periodStart, periodEnd, label));
                }
            }
            case "year" -> {
                // selectedYear phải được cung cấp khi filter là "year"
                if (selectedYear == null) {
                    throw new IllegalArgumentException("selectedYear is required for year filter");
                }

                // Lặp qua các năm từ selectedYear đến năm hiện tại
                for (int year = selectedYear; year <= currentYear; year++) {
                    LocalDate yearStart = LocalDate.of(year, 1, 1);
                    LocalDate yearEnd = LocalDate.of(year, 12, 31);

                    Instant periodStart = yearStart.atStartOfDay(ZoneOffset.UTC).toInstant();
                    Instant periodEnd = yearEnd.atTime(23, 59, 59).atZone(ZoneOffset.UTC).toInstant();

                    responses.add(getStatisticsResponse(periodStart, periodEnd, String.valueOf(year)));
                }
            }
            default -> throw new IllegalArgumentException("Invalid filter type: " + filter);
        }

        return responses;
    }

    private StatisticsResponse getStatisticsResponse(Instant start, Instant end, String label) {
        long newUsers = userRepository.countByCreatedAtBetween(start, end);
        long newStories = storyRepository.countApprovedStories(start, end);
        long transactionCount = walletTransactionRepository.countByCreatedAtBetween(start, end);

        StatisticsResponse response = new StatisticsResponse();
        response.setPeriodLabel(label);
        response.setNewUsers(newUsers);
        response.setNewStories(newStories);
        response.setTransactionCount(transactionCount);
        return response;
    }
}
