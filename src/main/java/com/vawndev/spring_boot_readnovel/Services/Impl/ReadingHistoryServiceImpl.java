package com.vawndev.spring_boot_readnovel.Services.Impl;

import com.vawndev.spring_boot_readnovel.Entities.ReadingHistory;
import com.vawndev.spring_boot_readnovel.Entities.Story;
import com.vawndev.spring_boot_readnovel.Entities.User;
import com.vawndev.spring_boot_readnovel.Repositories.ReadingHistoryRepository;
import com.vawndev.spring_boot_readnovel.Services.ReadingHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReadingHistoryServiceImpl implements ReadingHistoryService {

    private final ReadingHistoryRepository readingHistoryRepository;

    @Override
    @Transactional
    public void saveReadingHistory(String userId, String storyId, String chapterId) {
        // Tìm lịch sử đọc hiện tại của người dùng cho truyện cụ thể
        Optional<ReadingHistory> existingHistory = readingHistoryRepository.findByUserIdAndStoryId(userId, storyId);

        // Nếu không tồn tại, tạo mới
        ReadingHistory history = existingHistory.orElseGet(() -> ReadingHistory.builder()
                .user(User.builder().id(userId).build()) // Sử dụng builder của User
                .story(Story.builder().id(storyId).build()) // Sử dụng builder của Story
                .build());

        // Cập nhật thông tin chương và thời gian đọc
        history.setChapterId(chapterId);
        history.setLastReadAt(LocalDateTime.now());

        // Lưu vào cơ sở dữ liệu
        readingHistoryRepository.save(history);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReadingHistory> getReadingHistory(String userId) {
        // Lấy danh sách lịch sử đọc của người dùng
        return readingHistoryRepository.findByUserId(userId);
    }
}