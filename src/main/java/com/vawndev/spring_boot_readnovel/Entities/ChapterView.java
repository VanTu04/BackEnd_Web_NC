package com.vawndev.spring_boot_readnovel.Entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "chapter_view", uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "chapter_id"}))
public class ChapterView {
    // lưu trữ thời gian truy cập chapter để tính lượt view

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "chapter_id", nullable = false)
    private Long chapterId;

    @Column(name = "viewed_at", nullable = false)
    private LocalDateTime viewedAt;


    /*
    private final ChapterViewRepository chapterViewRepository;
    private final ChapterRepository chapterRepository;

    @Transactional
    public void recordChapterView(Long userId, Long chapterId) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime timeLimit = now.minusMinutes(15);

        Optional<ChapterView> viewOpt = chapterViewRepository.findByUserAndChapter(userId, chapterId);

        if (viewOpt.isEmpty()) {
            // Nếu chưa có bản ghi nào -> Thêm mới & tăng view
            ChapterView newView = new ChapterView(userId, chapterId, now);
            chapterViewRepository.save(newView);
            chapterRepository.incrementViewCount(chapterId);
        } else {
            ChapterView existingView = viewOpt.get();
            if (existingView.getViewedAt().isBefore(timeLimit)) {
                // Nếu đã quá 15 phút -> Cập nhật viewedAt & tăng view
                existingView.setViewedAt(now);
                chapterViewRepository.save(existingView);
                chapterRepository.incrementViewCount(chapterId);
            }
            // Nếu chưa quá 15 phút thì không làm gì
        }
    }
     */
}
