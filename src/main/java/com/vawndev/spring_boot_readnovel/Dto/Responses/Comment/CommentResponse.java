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

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CommentResponse {

    private User user;

    private Story story;

    private Chapter chapter;

    private Comment parentComment;

    private String content;

    private boolean isDeleted;

    private User deletedBy;
}
