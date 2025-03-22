package com.vawndev.spring_boot_readnovel.Services.Impl;

import com.vawndev.spring_boot_readnovel.Dto.Requests.CommentRequest;
import com.vawndev.spring_boot_readnovel.Dto.Responses.Comment.CommentResponse;
import com.vawndev.spring_boot_readnovel.Entities.Chapter;
import com.vawndev.spring_boot_readnovel.Entities.Comment;
import com.vawndev.spring_boot_readnovel.Entities.Story;
import com.vawndev.spring_boot_readnovel.Entities.User;
import com.vawndev.spring_boot_readnovel.Exceptions.AppException;
import com.vawndev.spring_boot_readnovel.Exceptions.ErrorCode;
import com.vawndev.spring_boot_readnovel.Mappers.CommentMapper;
import com.vawndev.spring_boot_readnovel.Repositories.ChapterRepository;
import com.vawndev.spring_boot_readnovel.Repositories.CommentRepository;
import com.vawndev.spring_boot_readnovel.Repositories.StoryRepository;
import com.vawndev.spring_boot_readnovel.Repositories.UserRepository;
import com.vawndev.spring_boot_readnovel.Services.CommentService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final StoryRepository storyRepository;
    private final ChapterRepository chapterRepository;
    private final CommentMapper commentMapper;

    @Override
    public CommentResponse addComment(CommentRequest commentRequest) {
        User user = userRepository.findById(commentRequest.getUserId()).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        Story story = null;
        Chapter chapter = null;
        Comment parentComment = null;
        if(commentRequest.getStoryId()!=null){
            story = storyRepository.findById(commentRequest.getStoryId()).orElse(null);
        }
        if(commentRequest.getChapterId()!=null){
            chapter = chapterRepository.findById(commentRequest.getChapterId()).orElse(null);
        }
        if(commentRequest.getParentCommentId()!=null){
            parentComment = commentRepository.findById(commentRequest.getParentCommentId()).orElse(null);
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
        return commentMapper.toCommentResponse(commentRepository.save(comment));
    }

    @Override
    public Page<CommentResponse> getCommentByChapter(String chapterId, Pageable pageable) {
        return commentRepository.findByChapterIdAndIsDeletedFalse(
                chapterId,
                pageable
        ).map(commentMapper::toCommentResponse);
    }

    @Override
    public Page<CommentResponse> getCommentByStory(String storyId, Pageable pageable) {
        return commentRepository.findByStoryIdAndIsDeletedFalse(
                storyId,
                pageable
        ).map(commentMapper::toCommentResponse);
    }

    @Override
    public Page<CommentResponse> getReplies(String parentCommentId, Pageable pageable) {
        return commentRepository.findByParentCommentId(
                parentCommentId,
                pageable
        ).map(commentMapper::toCommentResponse);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public Page<CommentResponse> getAllCommentByStory(String storyId, Pageable pageable) {
        return commentRepository.findByStoryId(storyId, pageable).map(commentMapper::toCommentResponse);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public Page<CommentResponse> getAllCommentByChapter(String chapterId, Pageable pageable) {
        return commentRepository.findByChapterId(
                chapterId,
                pageable
        ).map(commentMapper::toCommentResponse);
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
}
