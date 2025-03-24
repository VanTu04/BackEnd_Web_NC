package com.vawndev.spring_boot_readnovel.Services.Impl;

import com.vawndev.spring_boot_readnovel.Constants.PredefinedRole;
import com.vawndev.spring_boot_readnovel.Repositories.StoryRepository;
import com.vawndev.spring_boot_readnovel.Repositories.UserRepository;
import com.vawndev.spring_boot_readnovel.Services.ReportService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Service
@Transactional
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final StoryRepository storyRepository;
    private final UserRepository userRepository;

    @Override
    public Map<String, Object> statistics(Integer month, Integer year) {
        LocalDate now = LocalDate.now();
        year = (year == null) ? now.getYear() : year;
        month = (month == null) ? now.getMonthValue() : month;

        Map<String, Object> result = new HashMap<>();

//        Số lượng truyện được duyệt theo tháng
        Long approvedStories = storyRepository.countApprovedStories(month, year);
        result.put("approvedStories", approvedStories);

//        Số lượng truyện bị từ chối theo tháng
        Long rejectedStories = storyRepository.countRejectedStories(month, year);
        result.put("rejectedStories", rejectedStories);

//        Số lượng user đăng kí mới theo tháng
        Long newUsers = userRepository.CountNewUsersByRole(PredefinedRole.CUSTOMER_ROLE, month, year);
        result.put("newUsers", newUsers);

        return result;
    }
}
