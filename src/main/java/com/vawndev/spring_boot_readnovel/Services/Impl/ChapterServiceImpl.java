package com.vawndev.spring_boot_readnovel.Services.Impl;

import com.vawndev.spring_boot_readnovel.Dto.Requests.Chapter.ChapterRequest;
import com.vawndev.spring_boot_readnovel.Dto.Requests.Chapter.ChapterUploadRequest;
import com.vawndev.spring_boot_readnovel.Dto.Requests.FILE.FileRequest;
import com.vawndev.spring_boot_readnovel.Dto.Responses.Chapter.ChapterResponses;
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
import org.springframework.stereotype.Service;

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
        Chapter chapter=chapterRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.INVALID_STORY));
        chapterRepository.delete(chapter);
    }

    @Override
    public ChapterResponses getChapterDetail(String id) {
        Chapter chapter = chapterRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.INVALID_STORY));

        long currentTimestamp = (System.currentTimeMillis() / 1000) + 60;

        Map<String, String> params = new HashMap<>();
        params.put("public_id", id);
        params.put("timestamp", String.valueOf(currentTimestamp - 300));
        params.put("expired_at", String.valueOf(currentTimestamp));

        // Tạo chữ ký đồng nhất
        String signature = securityUtils.GenerateSignature(params);

        // URL bảo mật
        String secureUrl = "http://localhost:8080/Chapter/proxy?sig=" + signature + "&id=" + id +"&currentTimestamp=" + currentTimestamp;

        return ChapterResponses.builder()
                .title(chapter.getTitle())
                .content(chapter.getContent())
                .price(chapter.getPrice())
                .image_proxy(secureUrl)
                .signature(signature)
                .expiredAt(300)
                .build();
    }




}
