package com.vawndev.spring_boot_readnovel.Repositories;

import com.vawndev.spring_boot_readnovel.Entities.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, String> {

    Page<Comment> findByStoryIdAndParentCommentIsNullAndIsDeletedFalse(String storyId, Pageable pageable); // Lấy comment theo truyện (chỉ comment chưa xoá)

    List<Comment> findByStoryIdAndParentCommentIsNotNullAndIsDeletedFalse(String storyId);

    Page<Comment> findByChapterIdAndIsDeletedFalse(String chapterId, Pageable pageable); // Lấy comment theo chương (chỉ comment chưa xoá)

    Page<Comment> findByStoryId(String storyId, Pageable pageable); // Admin lấy tất cả comment theo truyện

    Page<Comment> findByChapterId(String chapterId, Pageable pageable);

    Page<Comment> findByParentCommentIdAndDeletedFalse(String parentId, Pageable pageable);
}
