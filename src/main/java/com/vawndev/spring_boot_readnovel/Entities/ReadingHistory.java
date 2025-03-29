package com.vawndev.spring_boot_readnovel.Entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;
import java.time.LocalDateTime;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReadingHistory extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "story_id", nullable = false)
    private Story story;

    @Column(name = "chapter_id", nullable = false)
    private String chapterId;

    @Column(name = "last_read_at", nullable = false)
    private LocalDateTime lastReadAt;
}