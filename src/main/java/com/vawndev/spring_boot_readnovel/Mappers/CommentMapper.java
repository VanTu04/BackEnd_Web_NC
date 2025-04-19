package com.vawndev.spring_boot_readnovel.Mappers;

import com.vawndev.spring_boot_readnovel.Dto.Responses.Comment.CommentResponse;
import com.vawndev.spring_boot_readnovel.Entities.Comment;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CommentMapper {
    CommentResponse toCommentResponse(Comment comment);
}
