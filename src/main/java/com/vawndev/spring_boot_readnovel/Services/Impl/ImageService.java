package com.vawndev.spring_boot_readnovel.Services.Impl;

import com.vawndev.spring_boot_readnovel.Entities.Chapter;
import com.vawndev.spring_boot_readnovel.Entities.File;
import com.vawndev.spring_boot_readnovel.Enum.StoryType;
import com.vawndev.spring_boot_readnovel.Exceptions.AppException;
import com.vawndev.spring_boot_readnovel.Exceptions.ErrorCode;
import com.vawndev.spring_boot_readnovel.Repositories.ChapterRepository;
import com.vawndev.spring_boot_readnovel.Repositories.FileRepository;
import com.vawndev.spring_boot_readnovel.Utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.*;


@Service
@RequiredArgsConstructor
public class ImageService {
    private final FileRepository fileRepository;
    private final ChapterRepository chapterRepository;

    public Map<String, String> getFile(List<String> ids, String chapterId) {
        Chapter chapter = chapterRepository.findById(chapterId)
                .orElseThrow(() -> new AppException(ErrorCode.INVALID_CHAPTER));
        StoryType getTypeStory = chapter.getStory().getType();

        Map<String, String> fileMap = new HashMap<>();

        // Map file extension to MINE type
        Map<String, String> mimeTypes = Map.of(
                "png", "image/png",
                "jpg", "image/jpeg",
                "jpeg", "image/jpeg",
                "docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
        );

        for (String id : ids) {
            fileRepository.findById(id).ifPresentOrElse(image -> {
                try (InputStream inputStream = new URL(image.getUrl()).openStream()) {
                    byte[] bytes = inputStream.readAllBytes();
                    String base64 = Base64.getEncoder().encodeToString(bytes);

                    String fileUrl = image.getUrl().toLowerCase();
                    String fileExtension = fileUrl.substring(fileUrl.lastIndexOf('.') + 1);

                    String mimeType = mimeTypes.getOrDefault(fileExtension, "application/octet-stream");

                    fileMap.put(id, "data:" + mimeType + ";base64," + base64);
                } catch (IOException e) {
                    throw new AppException(ErrorCode.FILE_NOT_FOUND);
                }
            }, () -> System.err.println("❌ Không tìm thấy file với ID: " + id));
        }

        return fileMap;
    }

}

