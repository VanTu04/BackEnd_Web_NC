package com.vawndev.spring_boot_readnovel.Entities;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "comments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Comment extends BaseEntity{

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // Người viết comment

    @ManyToOne
    @JoinColumn(name = "story_id")
    private Story story; // Comment theo truyện

    @ManyToOne
    @JoinColumn(name = "chapter_id")
    private Chapter chapter; // Comment theo chương

    @ManyToOne
    @JoinColumn(name = "parent_id")
    private Comment parentComment;

    @OneToMany(mappedBy = "parentComment", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> replies = new ArrayList<>();

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false)
    private boolean isDeleted = false;

    @ManyToOne
    @JoinColumn(name = "deleted_by")
    private User deletedBy; // Người xoá comment (nếu bị xoá)

}

