package com.vawndev.spring_boot_readnovel.Services.Impl;

import com.vawndev.spring_boot_readnovel.Dto.Requests.CommentRequest;
import com.vawndev.spring_boot_readnovel.Dto.Responses.Comment.CommentResponse;
import com.vawndev.spring_boot_readnovel.Entities.Chapter;
import com.vawndev.spring_boot_readnovel.Entities.Comment;
import com.vawndev.spring_boot_readnovel.Entities.Story;
import com.vawndev.spring_boot_readnovel.Entities.User;
import com.vawndev.spring_boot_readnovel.Exceptions.AppException;
import com.vawndev.spring_boot_readnovel.Exceptions.ErrorCode;
import com.vawndev.spring_boot_readnovel.Repositories.ChapterRepository;
import com.vawndev.spring_boot_readnovel.Repositories.CommentRepository;
import com.vawndev.spring_boot_readnovel.Repositories.StoryRepository;
import com.vawndev.spring_boot_readnovel.Repositories.UserRepository;
import com.vawndev.spring_boot_readnovel.Services.CommentService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final StoryRepository storyRepository;
    private final ChapterRepository chapterRepository;

    @Override
    public CommentResponse addComment(CommentRequest commentRequest) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        var user = userRepository.findByEmail(email).orElseThrow(() -> new AppException(ErrorCode.UNAUTHENTICATED));

        Story story = null;
        Chapter chapter = null;
        Comment parentComment = null;
        if(commentRequest.getStoryId()!=null){
            story = storyRepository.findById(commentRequest.getStoryId()).orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND, "Story"));
        }
        if(commentRequest.getChapterId()!=null){
            chapter = chapterRepository.findById(commentRequest.getChapterId()).orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND, "Chapter"));
        }
        if(commentRequest.getParentCommentId()!=null){
            parentComment = commentRepository.findById(commentRequest.getParentCommentId()).orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND, "ParentComment"));
        }
        if (story == null && chapter == null) {
            throw new AppException(ErrorCode.OBJECT_INVAILD, "StoryId or ChapterId");
        }
        Comment comment = Comment.builder()
                .user(user)
                .story(story)
                .chapter(chapter)
                .parentComment(parentComment)
                .content(commentRequest.getContent())
                .isDeleted(false)
                .build();
        return this.toCommentResponse(commentRepository.save(comment));
    }

    private CommentResponse toCommentResponse(Comment comment) {
        return CommentResponse.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .isDeleted(comment.isDeleted())
                .createdAt(comment.getCreatedAt())
                .deletedBy(comment.getDeletedBy() != null ? comment.getDeletedBy().getFullName() : null)
                .userId(comment.getUser().getId())
                .fullUserName(comment.getUser().getFullName())
                .imageUser(comment.getUser().getImageUrl())
                .storyId(comment.getStory() != null ? comment.getStory().getId() : null)
                .storyTitle(comment.getStory() != null ? comment.getStory().getTitle() : null)
                .chapterId(comment.getChapter() != null ? comment.getChapter().getId() : null)
                .chapterTitle(comment.getChapter() != null ? comment.getChapter().getTitle() : null)
                .parentCommentId(comment.getParentComment() != null ? comment.getParentComment().getId() : null)
                // Replies sẽ được gắn thủ công sau
                .replies(new ArrayList<>())
                .build();
    }

//     đã fetch replies khi lấy comment cha, có thể gắn replies trực tiếp
    private CommentResponse mapWithReplies(Comment comment) {
        CommentResponse parent = toCommentResponse(comment);

        List<CommentResponse> replyResponses = comment.getReplies().stream()
                .filter(reply -> !reply.isDeleted())
                .map(this::toCommentResponse)
                .collect(Collectors.toList());

        parent.setReplies(replyResponses);
        return parent;
    }


    @Override
    public Page<CommentResponse> getCommentByChapter(String chapterId, Pageable pageable) {
        return commentRepository.findByChapterIdAndIsDeletedFalse(
                chapterId,
                pageable
        ).map(this::toCommentResponse);
    }

    @Override
    public Page<CommentResponse> getCommentByStory(String storyId, Pageable pageable) {
        Page<Comment> parentPage = commentRepository
                .findByStoryIdAndParentCommentIsNullAndIsDeletedFalse(storyId, pageable);

        List<CommentResponse> commentResponses = parentPage.getContent().stream()
                .map(this::mapWithReplies)
                .collect(Collectors.toList());

        return new PageImpl<>(commentResponses, pageable, parentPage.getTotalElements());
    }

    @Override
    public Page<CommentResponse> getReplies(String parentCommentId, Pageable pageable) {
        return commentRepository.findByParentCommentIdAndDeletedFalse(
                parentCommentId,
                pageable
        ).map(this::toCommentResponse);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public Page<CommentResponse> getAllCommentByStory(String storyId, Pageable pageable) {
        return commentRepository.findByStoryId(storyId, pageable).map(this::toCommentResponse);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public Page<CommentResponse> getAllCommentByChapter(String chapterId, Pageable pageable) {
        return commentRepository.findByChapterId(
                chapterId,
                pageable
        ).map(this::toCommentResponse);
    }

    @Override
    public void deleteComment(String userId, String commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND, "Comment"));

        if (!comment.isDeleted()) {
            User adminOrAuthor = userRepository.findById(userId)
                    .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND, "User"));
            comment.setDeleted(true);
            comment.setDeletedBy(adminOrAuthor);
            commentRepository.save(comment);
        }
    }

    @Override
    public String test(String id) {
        commentRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND, "Comment"));
        return "ok";
    }
}
