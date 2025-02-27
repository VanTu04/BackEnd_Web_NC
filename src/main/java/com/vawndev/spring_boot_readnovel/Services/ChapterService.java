package com.vawndev.spring_boot_readnovel.Services;

import com.vawndev.spring_boot_readnovel.Entities.Chapter;

import java.util.List;

public interface ChapterService {
    Chapter addChapter(String storyId, Chapter chapter);

    Chapter updateChapter(String id, Chapter chapterDetails);

    //void deleteChapter(String id);

    List<Chapter> getAllChapters();
}