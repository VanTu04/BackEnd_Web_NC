package com.vawndev.spring_boot_readnovel.Services;

import com.vawndev.spring_boot_readnovel.Dto.Requests.CommentRequest;
import com.vawndev.spring_boot_readnovel.Dto.Requests.PageRequest;
import com.vawndev.spring_boot_readnovel.Dto.Responses.Comment.CommentResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CommentService {
    Page<CommentResponse> getCommentByChapter(String chapterId, Pageable pageable);
    Page<CommentResponse> getCommentByStory(String storyId, Pageable pageable);
    Page<CommentResponse> getReplies(String parentCommentId, Pageable pageable);
    Page<CommentResponse> getAllCommentByStory(String storyId, Pageable pageable);
    Page<CommentResponse> getAllCommentByChapter(String chapterId, Pageable pageable);
    CommentResponse addComment(CommentRequest commentRequest);
    void deleteComment(String userId, String commentId);
}
