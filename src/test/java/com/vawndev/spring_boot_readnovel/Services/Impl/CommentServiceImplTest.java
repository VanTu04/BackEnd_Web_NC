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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.Instant;
import java.util.Optional;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CommentServiceImplTest {
    private CommentServiceImpl commentService;

    @Mock
    private UserRepository userRepository;
    @Mock
    private StoryRepository storyRepository;
    @Mock
    private ChapterRepository chapterRepository;
    @Mock
    private CommentRepository commentRepository;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        commentService = new CommentServiceImpl(commentRepository, userRepository, storyRepository, chapterRepository);

        // Mock Security Context
        Authentication authentication = mock(Authentication.class);
        SecurityContext context = mock(SecurityContext.class);
        when(context.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("user@example.com");
        SecurityContextHolder.setContext(context);
    }

    // TEST 1: Story only
    @Test
    public void testAddComment_WithStoryOnly() {
        CommentRequest request = CommentRequest.builder()
                .content("Story comment")
                .storyId("s1")
                .build();

        User user = mockUser("u1", "Alice");
        Story story = mockStory("s1", "Title 1");
        Comment saved = mockComment("c1", "Story comment", user, story, null, null);

        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(storyRepository.findById("s1")).thenReturn(Optional.of(story));
        when(commentRepository.save(any())).thenReturn(saved);

        CommentResponse response = commentService.addComment(request);

        assertEquals("c1", response.getId());
        assertEquals("s1", response.getStoryId());
        assertNull(response.getChapterId());
    }

    // TEST 2: Chapter only
    @Test
    public void testAddComment_WithChapterOnly() {
        CommentRequest request = CommentRequest.builder()
                .content("Chapter comment")
                .chapterId("c1")
                .build();

        User user = mockUser("u2", "Bob");
        Chapter chapter = mockChapter("c1", "Chapter 1");
        Comment saved = mockComment("c2", "Chapter comment", user, null, chapter, null);

        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(chapterRepository.findById("c1")).thenReturn(Optional.of(chapter));
        when(commentRepository.save(any())).thenReturn(saved);

        CommentResponse response = commentService.addComment(request);

        assertEquals("c2", response.getId());
        assertEquals("c1", response.getChapterId());
        assertNull(response.getStoryId());
    }

    // TEST 3: Chapter + ParentComment (nested comment)
    @Test
    public void testAddComment_WithParentComment() {
        CommentRequest request = CommentRequest.builder()
                .content("Reply comment")
                .chapterId("c2")
                .parentCommentId("p1")
                .build();

        User user = mockUser("u3", "Charlie");
        Chapter chapter = mockChapter("c2", "Chapter 2");
        Comment parent = Comment.builder().id("p1").build();
        Comment saved = mockComment("c3", "Reply comment", user, null, chapter, parent);

        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(chapterRepository.findById("c2")).thenReturn(Optional.of(chapter));
        when(commentRepository.findById("p1")).thenReturn(Optional.of(parent));
        when(commentRepository.save(any())).thenReturn(saved);

        CommentResponse response = commentService.addComment(request);

        assertEquals("c3", response.getId());
        assertEquals("p1", response.getParentCommentId());
    }

    // TEST 4: Invalid (no storyId or chapterId) → Throw Exception
    @Test
    public void testAddComment_InvalidTarget_ShouldThrow() {
        CommentRequest request = CommentRequest.builder()
                .content("Invalid comment")
                .build();

        User user = mockUser("u4", "Daisy");
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));

        AppException ex = assertThrows(AppException.class, () -> {
            commentService.addComment(request);
        });

        assertEquals(ErrorCode.OBJECT_INVAILD, ex.getErrorCode());
    }

    @Test
    public void testAddComment_OnlyParentComment_ShouldThrowException() {
        // Tạo request chỉ có parentCommentId mà không có storyId hoặc chapterId
        CommentRequest request = CommentRequest.builder()
                .content("Orphan reply")
                .parentCommentId("p1")
                .build();

        User user = mockUser("u5", "David");
        Comment parent = Comment.builder().id("p1").build();

        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(commentRepository.findById("p1")).thenReturn(Optional.of(parent));

        AppException ex = assertThrows(AppException.class, () -> {
            commentService.addComment(request);
        });

        assertEquals(ErrorCode.OBJECT_INVAILD, ex.getErrorCode());
        assertEquals("StoryId or ChapterId must be a value", ex.getMessage());
    }

    @Test
    public void testAddComment_MissingStoryAndChapter_ShouldThrowException() {
        // Tạo request không có cả storyId và chapterId
        CommentRequest request = CommentRequest.builder()
                .content("Comment without storyId or chapterId")
                .build();

        // Mock user
        User user = mockUser("u10", "Ivy");
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));

        // Kiểm tra ngoại lệ khi không có cả storyId và chapterId
        AppException ex = assertThrows(AppException.class, () -> {
            commentService.addComment(request);
        });

        // Kiểm tra mã lỗi và thông điệp lỗi
        assertEquals(ErrorCode.OBJECT_INVAILD, ex.getErrorCode());
        assertEquals("StoryId or ChapterId must be a value", ex.getMessage());
    }

    @Test
    public void testAddComment_ParentCommentNotFound_ShouldThrow() {
        CommentRequest request = CommentRequest.builder()
                .content("Reply to non-existent parent comment")
                .parentCommentId("p99")
                .build();

        User user = mockUser("u9", "Hannah");
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(commentRepository.findById("p99")).thenReturn(Optional.empty());

        AppException ex = assertThrows(AppException.class, () -> {
            commentService.addComment(request);
        });

        assertEquals(ErrorCode.NOT_FOUND, ex.getErrorCode());
    }

    private User mockUser(String id, String name) {
        return User.builder().id(id).fullName(name).email("user@example.com").imageUrl("img.png").build();
    }

    private Story mockStory(String id, String title) {
        return Story.builder().id(id).title(title).build();
    }

    private Chapter mockChapter(String id, String title) {
        return Chapter.builder().id(id).title(title).build();
    }

    private Comment mockComment(String id, String content, User user, Story story, Chapter chapter, Comment parent) {
        return Comment.builder()
                .id(id)
                .content(content)
                .user(user)
                .story(story)
                .chapter(chapter)
                .parentComment(parent)
                .isDeleted(false)
                .createdAt(Instant.now())
                .build();
    }
}
