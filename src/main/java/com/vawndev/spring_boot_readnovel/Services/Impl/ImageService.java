package com.vawndev.spring_boot_readnovel.Services.Impl;

import com.vawndev.spring_boot_readnovel.Dto.Responses.ImageResponse;
import com.vawndev.spring_boot_readnovel.Entities.Image;
import com.vawndev.spring_boot_readnovel.Exceptions.AppException;
import com.vawndev.spring_boot_readnovel.Exceptions.ErrorCode;
import com.vawndev.spring_boot_readnovel.Repositories.ImageRepository;
import com.vawndev.spring_boot_readnovel.Utils.SecurityUtils;
import io.github.cdimascio.dotenv.Dotenv;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;



@Service
@RequiredArgsConstructor
public class ImageService {
    private final ImageRepository imageRepository;
    private final SecurityUtils securityUtils;

    public Map<String, byte[]> getImages(List<String> ids) {
        Map<String, byte[]> images = new HashMap<>();

        for (String id : ids) {
            Optional<Image> imageOpt = imageRepository.findById(id);

            if (imageOpt.isPresent()) {
                Image image = imageOpt.get();
                System.out.println("Found Image ID: " + id + " - URL: " + image.getUrl());

                try {
                    URL url = new URL(image.getUrl());
                    InputStream inputStream = url.openStream();
                    byte[] imageBytes = inputStream.readAllBytes();
                    inputStream.close();
                    images.put(id, imageBytes);
                } catch (IOException e) {
                    System.err.println("Lỗi tải ảnh từ URL: " + image.getUrl());
                    e.printStackTrace();
                }
            } else {
                System.err.println("❌ Không tìm thấy ảnh với ID: " + id);
            }
        }
        return images;
    }

}

