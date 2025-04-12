package com.vawndev.spring_boot_readnovel.Services;

import com.vawndev.spring_boot_readnovel.Dto.Requests.Chapter.ChapterUploadRequest;

public interface ChapterService {

    void addChapter(ChapterUploadRequest chapterUploadRequest ) ;
    void deleteChapter(String id);
}
