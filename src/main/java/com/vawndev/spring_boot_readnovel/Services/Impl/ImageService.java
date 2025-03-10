package com.vawndev.spring_boot_readnovel.Services.Impl;

import com.vawndev.spring_boot_readnovel.Dto.Responses.ImageResponse;
import com.vawndev.spring_boot_readnovel.Exceptions.AppException;
import com.vawndev.spring_boot_readnovel.Exceptions.ErrorCode;
import com.vawndev.spring_boot_readnovel.Repositories.ImageRepository;
import com.vawndev.spring_boot_readnovel.Utils.SecurityUtils;
import io.github.cdimascio.dotenv.Dotenv;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;



@Service
@RequiredArgsConstructor
public class ImageService {
    private final ImageRepository imageRepository;
    private final SecurityUtils securityUtils;

    public List<ImageResponse> getImages(String id, String sig,long currentTimestamp) {

        Map<String, String> params = new HashMap<>();
        params.put("public_id", id);
        params.put("timestamp", String.valueOf(currentTimestamp - 300));
        params.put("expired_at", String.valueOf(currentTimestamp));

        String expectedSignature = securityUtils.GenerateSignature(params);

        if (!expectedSignature.equals(sig)) {
            throw new RuntimeException(sig + "===" +expectedSignature);
        }

        // Lấy danh sách ảnh từ cơ sở dữ liệu
        List<ImageResponse> result = imageRepository.findByChapterId(id).stream()
                .map(img -> ImageResponse.builder().url(img.getUrl()).build())
                .collect(Collectors.toList());
        return result;
    }
}

