package com.vawndev.spring_boot_readnovel.Services;

import com.vawndev.spring_boot_readnovel.Dto.Requests.Chapter.ChapterUploadRequest;
import com.vawndev.spring_boot_readnovel.Dto.Responses.Chapter.ChapterResponses;

public interface ChapterService {
    ChapterResponses getChapterDetail(String id);
    String addChapter(ChapterUploadRequest chapterUploadRequest ) ;
    void deleteChapter(String id);
}
