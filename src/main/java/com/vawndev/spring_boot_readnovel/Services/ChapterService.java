package com.vawndev.spring_boot_readnovel.Services;

import com.vawndev.spring_boot_readnovel.Dto.Requests.Chapter.ChapterUploadRequest;
import com.vawndev.spring_boot_readnovel.Dto.Responses.Chapter.ChapterResponseDetail;

public interface ChapterService {
    ChapterResponseDetail getChapterDetail(String id,String Ip);
    String addChapter(ChapterUploadRequest chapterUploadRequest ,String tokenBearer) ;
    void deleteChapter(String id,String email,String tokenBearer );
}
