package com.vawndev.spring_boot_readnovel.Services.Impl;

import com.vawndev.spring_boot_readnovel.Entities.Chapter;
import com.vawndev.spring_boot_readnovel.Entities.File;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;



@Service
@RequiredArgsConstructor
public class ImageService {
    private final FileRepository fileRepository;
    private final ChapterRepository chapterRepository;

        public Map<String, byte[]> getFile(List<String> ids,String chapter_id) {
            Chapter chapter = chapterRepository.findById(chapter_id).orElseThrow(()->new AppException(ErrorCode.INVALID_CHAPTER));
            Map<String, byte[]> file = new HashMap<>();
                for (String id : ids) {
                    Optional<File> imageOpt = fileRepository.findById(id);
                    if (imageOpt.isPresent()) {
                        File image = imageOpt.get();
                        try {
                            URL url = new URL(image.getUrl());
                            InputStream inputStream = url.openStream();
                            byte[] imageBytes = inputStream.readAllBytes();
                            inputStream.close();
                            file.put(id, imageBytes);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        System.err.println("❌ Không tìm thấy ảnh với ID: " + id);
                    }
            }
            return file;
        }

}

