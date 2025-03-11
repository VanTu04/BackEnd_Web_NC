package com.vawndev.spring_boot_readnovel.Services.Impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vawndev.spring_boot_readnovel.Dto.Requests.Chapter.ChapterRequest;
import com.vawndev.spring_boot_readnovel.Dto.Requests.Chapter.ChapterUploadRequest;
import com.vawndev.spring_boot_readnovel.Dto.Requests.FILE.FileRequest;
import com.vawndev.spring_boot_readnovel.Dto.Responses.Chapter.ChapterResponses;
import com.vawndev.spring_boot_readnovel.Dto.Responses.ImageResponse;
import com.vawndev.spring_boot_readnovel.Entities.Chapter;
import com.vawndev.spring_boot_readnovel.Entities.Image;
import com.vawndev.spring_boot_readnovel.Entities.Story;
import com.vawndev.spring_boot_readnovel.Exceptions.AppException;
import com.vawndev.spring_boot_readnovel.Exceptions.ErrorCode;
import com.vawndev.spring_boot_readnovel.Repositories.ChapterRepository;
import com.vawndev.spring_boot_readnovel.Repositories.ImageRepository;
import com.vawndev.spring_boot_readnovel.Repositories.StoryRepository;
import com.vawndev.spring_boot_readnovel.Services.ChapterService;
import com.vawndev.spring_boot_readnovel.Utils.SecurityUtils;
import io.github.cdimascio.dotenv.Dotenv;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;



@Service
@RequiredArgsConstructor
public class ChapterServiceImpl implements ChapterService {

    private final CloundServiceImpl cloundServiceImpl;
    private final StoryRepository storyRepository;
    private final ChapterRepository chapterRepository;
    private final ImageRepository imageRepository;
    private final SecurityUtils securityUtils;


    @Override
    public void addChapter(ChapterUploadRequest chapterUploadRequest) {
        FileRequest freq = chapterUploadRequest.getFile();
        ChapterRequest creq = chapterUploadRequest.getChapter();
        try {
            Story story = storyRepository.findById(creq.getStory_id())
                    .orElseThrow(() -> new AppException(ErrorCode.INVALID_STORY));

            List<String> listUrl;
            try {
                listUrl = cloundServiceImpl.getUrlChapterAfterUpload(freq);
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

            List<Image> imageList = listUrl.stream()
                    .map(url -> Image.builder()
                            .url(url)
                            .chapter(savedChapter)
                            .build())
                    .collect(Collectors.toList());

            imageRepository.saveAll(imageList);
        }catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteChapter(String id) {
        Chapter chapter=chapterRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.INVALID_CHAPTER));
        chapterRepository.delete(chapter);
    }

    @Override
    public ChapterResponses getChapterDetail(String id) {
        Chapter chapter = chapterRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.INVALID_DOB));
        List<ImageResponse> images =  imageRepository.findByChapterId(id).stream()
                .map(img -> ImageResponse.builder().id(img.getId()).build())
                .collect(Collectors.toList());
        return ChapterResponses.builder()
                .title(chapter.getTitle())
                .content(chapter.getContent())
                .price(chapter.getPrice())
                .images(images)
                .build();
    }


}
