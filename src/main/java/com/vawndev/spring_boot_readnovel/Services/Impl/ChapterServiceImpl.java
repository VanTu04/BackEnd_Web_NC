package com.vawndev.spring_boot_readnovel.Services.Impl;

import com.vawndev.spring_boot_readnovel.Dto.Requests.Chapter.ChapterRequest;
import com.vawndev.spring_boot_readnovel.Dto.Requests.Chapter.ChapterUploadRequest;
import com.vawndev.spring_boot_readnovel.Dto.Requests.FILE.FileRequest;
import com.vawndev.spring_boot_readnovel.Dto.Responses.Chapter.ChapterResponses;
import com.vawndev.spring_boot_readnovel.Dto.Responses.FileResponse;
import com.vawndev.spring_boot_readnovel.Entities.Chapter;
import com.vawndev.spring_boot_readnovel.Entities.File;
import com.vawndev.spring_boot_readnovel.Entities.Story;
import com.vawndev.spring_boot_readnovel.Exceptions.AppException;
import com.vawndev.spring_boot_readnovel.Exceptions.ErrorCode;
import com.vawndev.spring_boot_readnovel.Repositories.ChapterRepository;
import com.vawndev.spring_boot_readnovel.Repositories.FileRepository;
import com.vawndev.spring_boot_readnovel.Repositories.StoryRepository;
import com.vawndev.spring_boot_readnovel.Services.ChapterService;
import com.vawndev.spring_boot_readnovel.Services.CloundService;
import com.vawndev.spring_boot_readnovel.Utils.FileUpload;
import com.vawndev.spring_boot_readnovel.Utils.SecurityUtils;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;



@Service
@RequiredArgsConstructor
public class ChapterServiceImpl implements ChapterService {

    private final CloundService cloundService;
    private final StoryRepository storyRepository;
    private final ChapterRepository chapterRepository;
    private final FileRepository fileRepository;


    @Override
    public String addChapter(ChapterUploadRequest chapterUploadRequest) {
        FileRequest freq = chapterUploadRequest.getFile();
        ChapterRequest creq = chapterUploadRequest.getChapter();
        try {
            Story story = storyRepository.findById(creq.getStory_id())
                    .orElseThrow(() -> new AppException(ErrorCode.INVALID_STORY));

            List<String> listUrl;
            try {
                listUrl = cloundService.getUrlChapterAfterUpload(freq);
            } catch (IOException e) {
                throw new RuntimeException("Error uploading to Cloudinary", e);
            }
            Chapter chapter = Chapter.builder()
                    .title(creq.getTitle())
                    .content(creq.getContent())
                    .price(creq.getPrice())
                    .story(story)
                    .build();
            Chapter savedChapter = chapterRepository.save(chapter);

            List<File> imageList = listUrl.stream()
                    .map(url -> File.builder()
                            .url(url)
                            .chapter(savedChapter)
                            .build())
                    .collect(Collectors.toList());

            fileRepository.saveAll(imageList);
            return chapter.getId();

        }catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteChapter(String id) {
        Chapter chapter = chapterRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.INVALID_CHAPTER));
        List<File> files = fileRepository.findByChapterId(chapter.getId());

        // get list private IDs from URLs
        List<String> publicId = files.stream()
                .map(file -> FileUpload.extractPublicId(file.getUrl()))
                .collect(Collectors.toList());

        // delete file from Cloudinary
        try {
            cloundService.removeUrlOnChapterDelete(publicId);
        } catch (Exception e) {
            throw new RuntimeException("Error delete file from Cloudinary: " + e.getMessage());
        }

        // Xóa file trong database
        fileRepository.deleteAll(files);

        // Xóa chapter
        chapterRepository.delete(chapter);
    }


    @Override
    public ChapterResponses getChapterDetail(String id) {
        Chapter chapter = chapterRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.INVALID_CHAPTER));
        return ChapterResponses.builder()
                .id(chapter.getId())
                .title(chapter.getTitle())
                .content(chapter.getContent())
                .price(chapter.getPrice())
                .files(chapter.getFiles().stream().map(file->FileResponse.builder().id(file.getId()).build()).collect(Collectors.toList()))
                .build();
    }


}
