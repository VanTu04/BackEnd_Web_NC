package com.vawndev.spring_boot_readnovel.Dto.Requests;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommentRequest {
    @NotBlank(message = "OBJECT_INVAILD")
    private String storyId;
    private String chapterId;
    private String parentCommentId;
    @NotBlank(message = "OBJECT_IMVAILD")
    @Size(min = 3, message = "OBJECT_IMVAILD")
    private String content;
}
