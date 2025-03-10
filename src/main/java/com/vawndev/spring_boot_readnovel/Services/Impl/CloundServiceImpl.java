package com.vawndev.spring_boot_readnovel.Services.Impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.vawndev.spring_boot_readnovel.Dto.Requests.FILE.FileRequest;
import com.vawndev.spring_boot_readnovel.Dto.Requests.FILE.ImageCoverRequest;
import com.vawndev.spring_boot_readnovel.Services.CloundService;
import com.vawndev.spring_boot_readnovel.Utils.FileUpload;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CloundServiceImpl implements CloundService {

    private final Cloudinary cloudinary;

    @Override
    public List<String> getUrlChapterAfterUpload(FileRequest req) throws IOException {
        if (req == null || req.getFile() == null || req.getFile().isEmpty()) {
            throw new IllegalArgumentException("Không có file để upload");
        }

        List<String> fileUrls = new ArrayList<>();

        Set<String> allowedImageTypes = Set.of("image/jpeg", "image/png", "image/jpg");
        Set<String> allowedDocTypes = Set.of("application/vnd.openxmlformats-officedocument.wordprocessingml.document");

        Map<String, String> resourceMap = Map.of(
                "image/jpeg", "image",
                "image/png", "image",
                "image/jpg", "image",
                "application/vnd.openxmlformats-officedocument.wordprocessingml.document", "raw"
        );

        Map<String, String> folderMap = Map.of(
                "image", "images",
                "raw", "documents"
        );

        for (MultipartFile file : req.getFile()) {
            if (file.isEmpty()) {
                throw new IllegalArgumentException("File " + file.getOriginalFilename() + " bị rỗng!");
            }

            // Lấy content type
            String contentType = file.getContentType();

            // Xác định resource type
            String resourceType = resourceMap.get(contentType);
            if (resourceType == null) {
                throw new IllegalArgumentException("File " + file.getOriginalFilename() + " không được phép upload.");
            }

            // Kiểm tra phần mở rộng file để đảm bảo chính xác
            String fileName = file.getOriginalFilename();
            if (fileName == null || !fileName.matches(".*\\.(jpg|jpeg|png|docx)$")) {
                throw new IllegalArgumentException("File " + fileName + " không có định dạng hợp lệ!");
            }

            String folder = folderMap.get(resourceType);

            try {
                // Upload lên Cloudinary
                Map<String, Object> uploadResult = cloudinary.uploader().upload(
                        file.getBytes(),
                        ObjectUtils.asMap(
                                "resource_type", resourceType,
                                "folder", folder,
                                "type", "private"
                        )
                );
                fileUrls.add(uploadResult.get("secure_url").toString());
            } catch (IOException e) {
                throw new RuntimeException("Lỗi upload file: " + file.getOriginalFilename(), e);
            }
        }

        return fileUrls;
    }



    @Override
    public String getUrlCoverAfterUpload(ImageCoverRequest cover) throws IOException {
        if (cover == null || cover.getImage_cover() == null) {
            throw new IllegalArgumentException("Không có ảnh để upload");
        }

        FileUpload.assertAllowed(cover.getImage_cover(), ".*\\.(jpg|jpeg|png)$");

        String resourceType = cover.Type().toString().toLowerCase();
        String folder = "covers";

        Map<String, Object> uploadResult = cloudinary.uploader().upload(
                cover.getImage_cover().getBytes(),
                ObjectUtils.asMap(
                        "resource_type", resourceType,
                        "folder", folder
                )
        );

        return uploadResult.get("secure_url").toString();
    }



}
