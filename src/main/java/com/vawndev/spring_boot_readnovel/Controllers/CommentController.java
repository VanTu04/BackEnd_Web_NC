package com.vawndev.spring_boot_readnovel.Controllers;

import com.vawndev.spring_boot_readnovel.Dto.Requests.CommentRequest;
import com.vawndev.spring_boot_readnovel.Dto.Responses.ApiResponse;
import com.vawndev.spring_boot_readnovel.Dto.Responses.Comment.CommentResponse;
import com.vawndev.spring_boot_readnovel.Services.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/comment")
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;

    @GetMapping("/admin/story/{storyId}")
    public ApiResponse<?> getCommentStoryByAdmin(@PathVariable String storyId,
                                                 @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<CommentResponse> result = commentService.getAllCommentByStory(storyId, pageable);
        return ApiResponse.<Page<CommentResponse>>builder().result(result).build();
    }

    @GetMapping("/admin/chapter/{chapterId}")
    public ApiResponse<?> getCommentChapterByAdmin(@PathVariable String chapterId,
                                                   @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<CommentResponse> result = commentService.getAllCommentByChapter(chapterId, pageable);
        return ApiResponse.<Page<CommentResponse>>builder().result(result).build();
    }

    @GetMapping("/story/{storyId}")
    public ApiResponse<?> getCommentStory(@PathVariable String storyId,
                                                 @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<CommentResponse> result = commentService.getCommentByStory(storyId, pageable);
        return ApiResponse.<Page<CommentResponse>>builder().result(result).build();
    }

    @GetMapping("/chapter/{chapterId}")
    public ApiResponse<?> getCommentChapter(@PathVariable String chapterId,
                                                   @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<CommentResponse> result = commentService.getCommentByChapter(chapterId, pageable);
        return ApiResponse.<Page<CommentResponse>>builder().result(result).build();
    }

    @GetMapping("/{parentCommentId}")
    public ApiResponse<?> getChildComment(@PathVariable String parentCommentId,
                                          @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.ASC) Pageable pageable){
        Page<CommentResponse> result = commentService.getReplies(parentCommentId, pageable);
        return ApiResponse.<Page<CommentResponse>>builder().result(result).build();
    }

    @PostMapping("/new-comment")
    public ApiResponse<?> addComment(@RequestBody CommentRequest commentRequest) {
        return ApiResponse.<CommentResponse>builder().result(commentService.addComment(commentRequest)).build();
    }

    @DeleteMapping("/")
    public ApiResponse<?> deleteComment(@RequestParam String commentId, @RequestParam String userId) {
        commentService.deleteComment(userId, commentId);
        return ApiResponse.<String>builder().message("Success").result("Success delete comment!").build();
    }

    @PostMapping("/test")
    public String testfunc(@RequestParam String id){
        return commentService.test(id);
    }
}
