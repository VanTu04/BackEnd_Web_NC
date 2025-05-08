package com.vawndev.spring_boot_readnovel.Services;

import com.vawndev.spring_boot_readnovel.Dto.Requests.ConditionRequest;
import com.vawndev.spring_boot_readnovel.Dto.Requests.Chapter.ChapterUploadRequest;
import com.vawndev.spring_boot_readnovel.Dto.Responses.Chapter.ChapterResponseDetail;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ChapterService {
    ChapterResponseDetail getChapterDetail(String id);

    String addChapter(ChapterUploadRequest chapterUploadRequest, List<MultipartFile> files);

    void deleteChapter(String id);

    void buyChapter(ConditionRequest req);

}
