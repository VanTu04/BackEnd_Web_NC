package com.vawndev.spring_boot_readnovel.Repositories;

import com.vawndev.spring_boot_readnovel.Entities.ChapterView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ChapterViewRepository extends JpaRepository<ChapterView, Long> {

    @Query("SELECT cv FROM ChapterView cv WHERE cv.userId = :userId AND cv.chapterId = :chapterId")
    Optional<ChapterView> findByUserAndChapter(@Param("userId") Long userId,
                                               @Param("chapterId") Long chapterId);
}
