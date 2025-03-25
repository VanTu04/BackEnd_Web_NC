package com.vawndev.spring_boot_readnovel.Controllers;

import com.vawndev.spring_boot_readnovel.Dto.Requests.Chapter.ChapterRequest;
import com.vawndev.spring_boot_readnovel.Dto.Requests.Chapter.ChapterUploadRequest;
import com.vawndev.spring_boot_readnovel.Dto.Requests.FILE.ImageFileRequest;
import com.vawndev.spring_boot_readnovel.Dto.Requests.FILE.RawFileRequest;
import com.vawndev.spring_boot_readnovel.Dto.Responses.ApiResponse;
import com.vawndev.spring_boot_readnovel.Dto.Responses.Chapter.ChapterResponseDetail;
import com.vawndev.spring_boot_readnovel.Services.ChapterService;
import com.vawndev.spring_boot_readnovel.Services.Impl.ImageService;
import com.vawndev.spring_boot_readnovel.Utils.Help.JsonHelper;

import com.vawndev.spring_boot_readnovel.Utils.IpUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

import org.springframework.http.MediaType;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/chapter")
@RequiredArgsConstructor
public class ChapterController {
    private final ChapterService chapterService;
    private final ImageService imageService;



    @PostMapping(value = "/add", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<String> addChapter(
            @RequestPart("chapterJson") String chapterJson,
            @RequestPart(value = "files", required = false) List<MultipartFile> uploadedFiles
    ) {
        ChapterRequest chapterRequest = JsonHelper.parseJson(chapterJson, ChapterRequest.class);
        ChapterUploadRequest uploadRequest = new ChapterUploadRequest();
        uploadRequest.setChapter(chapterRequest);

        String result = chapterService.addChapter(uploadRequest,uploadedFiles);
        return ApiResponse.<String>builder().message("Successfully!").result(result).build();
    }


    @DeleteMapping("/delete")
    public ApiResponse<String> deleteChapter(
            @RequestParam String id) {
        chapterService.deleteChapter(id);
        return ApiResponse.<String>builder().message("Successfully!").build();
    }

    @GetMapping("/{chapter_id}")
    public ApiResponse<ChapterResponseDetail> getChapter(@PathVariable String chapter_id ,@RequestHeader(value = "Authorization",required = false) String bearerToken) {
        ChapterResponseDetail result = chapterService.getChapterDetail(chapter_id,bearerToken);
        return ApiResponse.<ChapterResponseDetail>builder()
                .message("Successfully")
                .result(result)
                .build();
    }

    @GetMapping("/{chapter_id}/proxy")
    public ApiResponse<Map<String, String>>getChapterProxy(@PathVariable String chapter_id,@RequestParam List<String> ids) {
        Map<String,String> result = imageService.getFile(ids,chapter_id);
        return ApiResponse.<Map<String, String>>builder()
                .result(result)
                .message("Successfully!!")
                .build();
    }
}
