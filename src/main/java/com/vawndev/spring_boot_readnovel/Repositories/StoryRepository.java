package com.vawndev.spring_boot_readnovel.Repositories;

import com.vawndev.spring_boot_readnovel.Entities.Chapter;
import com.vawndev.spring_boot_readnovel.Entities.Story;
import com.vawndev.spring_boot_readnovel.Entities.User;
import com.vawndev.spring_boot_readnovel.Enum.IS_AVAILBLE;
import com.vawndev.spring_boot_readnovel.Enum.STORY_STATUS;

import jakarta.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface StoryRepository extends JpaRepository<Story, String> {
        @Query("""
                            SELECT s FROM Story s
                            JOIN s.categories c
                            WHERE c.id = :idCate
                            AND s.isBanned = FALSE
                             AND s.isVisibility = TRUE
                             AND s.isAvailable = 'ACCEPTED'
                            ORDER BY s.createdAt DESC
                        """)
        Page<Story> findAcceptedByCate(String idCate, Pageable pageable);

        @Query("""
                            SELECT s FROM Story s
                            WHERE s.author.id = :id
                            AND s.deleteAt IS NULL
                            ORDER BY s.createdAt DESC
                        """)
        Page<Story> findAllByAuthor(String id, Pageable pageable);

        @Query("""
                            SELECT s FROM Story s
                            WHERE s.author.email = :email
                            AND s.deleteAt IS NULL
                            AND s.isBanned = FALSE
                            AND s.isVisibility = TRUE
                            ORDER BY s.createdAt DESC
                        """)
        Page<Story> findByAuthor(String email, Pageable pageable);

        @Query("""
                            SELECT s FROM Story s
                            WHERE s.author.id = :id
                            AND s.deleteAt IS NOT NULL
                            ORDER BY s.deleteAt DESC
                        """)
        Page<Story> findAllTrashByAuthor(String id, Pageable pageable);

        @Transactional // Cần thiết để thực hiện update/delete
        @Modifying
        @Query("""
                         UPDATE Story s
                         SET s.deleteAt = :deleteAt
                         WHERE s.id = :id_story
                         AND s.author.id = :id_author
                        """)
        void toggleDeleteStory(
                        @Param("id_story") String idStory,
                        @Param("id_author") String idAuthor,
                        @Param("deleteAt") Instant deleteAt);

        @Query("""
                        SELECT s FROM Story s
                        WHERE s.author.id=:authorId
                        AND s.deleteAt IS NULL
                        ORDER BY s.createdAt DESC
                        """)
        Page<Story> findByAuthorId(String authorId, Pageable pageable);

        @Query("""
                        SELECT s FROM Story s
                        WHERE s.isVisibility = TRUE
                        AND s.isBanned = FALSE
                        AND s.id =:id
                        """)
        Optional<Story> findByAcceptId(@Param("id") String id);

        @Query("""
                        SELECT s FROM Story s
                        WHERE s.isBanned = FALSE
                        AND s.id =:id
                        """)
        Optional<Story> findByMyAcceptId(@Param("id") String id);

        @Query("""
                        SELECT s FROM Story s
                        WHERE s.id =:id
                        AND s.author.id=:id_author
                        """)
        Optional<Story> findByIdAndAuthor(String id, String id_author);

        @Query("""
                        SELECT s FROM Story s
                        WHERE s.isVisibility = TRUE
                        AND  s.isAvailable = :available
                        AND s.status IN :statusList
                        AND s.isBanned = FALSE
                        AND s.deleteAt IS NULL
                        ORDER BY s.createdAt ASC
                        """)
        Page<Story> findAccepted(
                        IS_AVAILBLE available,
                        List<STORY_STATUS> statusList,
                        Pageable pageable);

        @Query("""
                        SELECT s FROM Story s
                        WHERE s.isVisibility = TRUE
                        AND  s.isAvailable = :available
                        AND s.status IN :statusList
                            AND s.deleteAt IS NULL

                        AND s.isBanned = FALSE
                        AND s.id=:storyId
                        """)
        Optional<Story> findAcceptedId(
                        IS_AVAILBLE available,
                        List<STORY_STATUS> statusList,
                        String storyId);

        @Query("""
                        SELECT s FROM Story s
                        WHERE s.isVisibility = TRUE
                        AND  s.isAvailable = :available
                        AND s.status IN :statusList
                            AND s.deleteAt IS NULL

                        AND s.isBanned = FALSE
                        ORDER BY s.views DESC, s.rate DESC
                        """)
        List<Story> findTopStories(IS_AVAILBLE available,
                        List<STORY_STATUS> statusList);

        @Query("select count(*) from Story s where s.isAvailable = 'ACCEPTED' and s.updatedAt between :start and :end")
        Long countApprovedStories(@Param("start") Instant start, @Param("end") Instant end);

        @Query("""
                            SELECT s FROM Story s
                            WHERE s.isVisibility = TRUE
                            AND  s.isAvailable = :available
                            AND s.status IN :statusList
                                AND s.deleteAt IS NULL
                        AND s.isBanned = FALSE
                        ORDER BY s.views DESC
                        """)
        Page<Story> findMostViews(IS_AVAILBLE available,
                        List<STORY_STATUS> statusList,
                        Pageable pageable);

        @Query("""
                        SELECT s FROM Story s
                        WHERE s.isVisibility = TRUE
                        AND  s.isAvailable = :available
                        AND s.status IN :statusList
                        AND s.deleteAt IS NULL

                        AND s.isBanned = FALSE
                        ORDER BY s.updatedAt ASC
                        """)
        Page<Story> findUpdating(IS_AVAILBLE available,
                        List<STORY_STATUS> statusList,
                        Pageable pageable);

        Page<Story> findAll(Pageable pageable);

        @Query("select count(*) from Story s where s.isAvailable = 'ACCEPTED' and month(s.updatedAt) = :month and year(s.updatedAt) = :year")
        Long countApprovedStories(@Param("month") int month, @Param("year") int year);

        @Query("SELECT COUNT(s) FROM Story s WHERE s.isAvailable = 'REJECTED' AND MONTH(s.createdAt) = :month AND YEAR(s.createdAt) = :year")
        Long countRejectedStories(@Param("month") int month, @Param("year") int year);

        @Query("SELECT s FROM Story s JOIN s.categories c WHERE  s.isAvailable = 'ACCEPTED' AND s.isVisibility = TRUE AND s.isBanned = FALSE AND s.deleteAt IS NULL AND c.id IN :categoryIds ORDER BY s.id DESC")
        Page<Story> findAllByCategoriesIn(List<String> categoryIds, Pageable pageable);

        @Query("SELECT COUNT(c) FROM Chapter c JOIN c.story s WHERE s.id = :storyId")
        Long countChapters(String storyId);
}
