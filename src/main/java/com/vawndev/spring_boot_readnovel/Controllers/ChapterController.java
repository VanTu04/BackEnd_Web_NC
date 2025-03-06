package com.vawndev.spring_boot_readnovel.Controllers;

import com.vawndev.spring_boot_readnovel.Dto.Requests.Chapter.ChapterRequest;
import com.vawndev.spring_boot_readnovel.Dto.Requests.Chapter.ChapterUploadRequest;
import com.vawndev.spring_boot_readnovel.Dto.Requests.FILE.ImageFileRequest;
import com.vawndev.spring_boot_readnovel.Dto.Requests.FILE.RawFileRequest;
import com.vawndev.spring_boot_readnovel.Dto.Responses.ApiResponse;
import com.vawndev.spring_boot_readnovel.Services.ChapterService;
import com.vawndev.spring_boot_readnovel.Utils.Help.JsonHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/Chapter")
@RequiredArgsConstructor
public class ChapterController {
    private final ChapterService chapterService;

    @PostMapping(value = "/add", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<String> addChapter(
            @RequestPart("chapter") String chapterJson,
            @RequestPart(value = "image",required = false) List<MultipartFile> images,
            @RequestPart(value = "file",required = false) List<MultipartFile> files
    ) {

        ChapterRequest chapterRequest= JsonHelper.parseJson(chapterJson, ChapterRequest.class);
        ChapterUploadRequest uploadRequest = new ChapterUploadRequest();
        uploadRequest.setChapter(chapterRequest);
        if (files != null && !files.isEmpty()) {
            RawFileRequest rawUpload = new RawFileRequest();
            rawUpload.setFile(files);
            uploadRequest.setFile(rawUpload);
        }

        if (images != null && !images.isEmpty()) {
            ImageFileRequest imageUpload = new ImageFileRequest();
            imageUpload.setFile(images);
            uploadRequest.setFile(imageUpload);
        }

        chapterService.addChapter(uploadRequest);

        return ApiResponse.<String>builder().message("Successfully!").build();
    }


    @DeleteMapping("/delete")
    public ApiResponse<String> deleteChapter(@RequestParam String id) {
        chapterService.deleteChapter(id);
        return ApiResponse.<String>builder().message("Successfully!").build();
    }
}
