package com.vawndev.spring_boot_readnovel.Controllers;

import com.vawndev.spring_boot_readnovel.Dto.Requests.Chapter.ChapterRequest;
import com.vawndev.spring_boot_readnovel.Dto.Requests.Chapter.ChapterUploadRequest;
import com.vawndev.spring_boot_readnovel.Dto.Responses.ApiResponse;
import com.vawndev.spring_boot_readnovel.Services.ChapterService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/Chapter")
@RequiredArgsConstructor
public class ChapterController {
    private final ChapterService chapterService;

    @PostMapping("/add")
    public ApiResponse<String> addChapter(@RequestBody ChapterUploadRequest chapterUploadRequest) {
        chapterService.addChapter(chapterUploadRequest);
        return ApiResponse.<String>builder().message("Successfully!").build();
    }

    @DeleteMapping("/delete")
    public ApiResponse<String> deleteChapter(@RequestBody String id) {
        chapterService.deleteChapter(id);
        return ApiResponse.<String>builder().message("Successfully!").build();
    }
}
