package com.vawndev.spring_boot_readnovel.Dto.Requests;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommentRequest {
    @NotBlank(message = "OBJECT_INVAILD")
    private String userId;
    private String storyId;
    private String chapterId;
    private String parentCommentId;
    @NotBlank(message = "OBJECT_INVAILD")
    String content;
}
