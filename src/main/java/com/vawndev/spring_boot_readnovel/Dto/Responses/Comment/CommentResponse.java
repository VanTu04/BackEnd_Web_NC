package com.vawndev.spring_boot_readnovel.Dto.Responses.Comment;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.vawndev.spring_boot_readnovel.Entities.Chapter;
import com.vawndev.spring_boot_readnovel.Entities.Comment;
import com.vawndev.spring_boot_readnovel.Entities.Story;
import com.vawndev.spring_boot_readnovel.Entities.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CommentResponse {
    private String id;
    private String content;
    private boolean isDeleted;
    private String userId;
    private String fullUserName;
    private String imageUser;
    private Instant createdAt;
    private String deletedBy;
    private String storyId;
    private String storyTitle;
    private String chapterId;
    private String chapterTitle;
    private String parentCommentId;

    private List<CommentResponse> replies;
}
