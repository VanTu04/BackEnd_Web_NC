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

            String contentType = file.getContentType();

            // Dentermind resource type
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

        FileUpload.validFormatImageCover(cover.getImage_cover().getOriginalFilename());

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
    @Override
    public Map<String, String> removeUrlOnChapterDelete(List<String> privateIDs) {
        Map<String, String> deleteResults = new HashMap<>();

        for (String id : privateIDs) {
            try {
                // Dentermind resource (image or file raw)
                String[] parts = id.split("/");
                String resourceType = parts.length > 0 && "images".equals(parts[0]) ? "image" : "raw";

                // Remove the extension (if any)
                String idWithoutExt = id.contains(".") ? id.substring(0, id.lastIndexOf(".")) : id;


                // call API Cloudinary to remove file
                Map<String, Object> result = cloudinary.uploader().destroy(
                        idWithoutExt, // Giữ nguyên ID
                        ObjectUtils.asMap("invalidate", true, "resource_type", resourceType, "type", "private")
                );

                String status = result.get("result").toString();

                if ("not found".equals(status)) {
                    throw new RuntimeException("Cloudinary: ID not found - " + id);
                }

                deleteResults.put(id, status);
            } catch (Exception e) {
                deleteResults.put(id, "error");
                throw new RuntimeException("Error while deleting " + id, e);
            }
        }
        return deleteResults;
    }

    @Override
    public Map<String, String> removeUrlOnStory(String publicId) {
        Map<String, String> deleteResults = new HashMap<>();
            try {
                // Dentermind resource (image or file raw)
                String[] parts = publicId.split("/");
                String resourceType = parts.length > 0 && "images".equals(parts[0]) ? "image" : "raw";
                // Remove the extension (if any)
                String idWithoutExt = publicId.contains(".") ? publicId.substring(0, publicId.lastIndexOf(".")) : publicId;
                // call API Cloudinary to remove file
                Map<String, Object> result = cloudinary.uploader().destroy(
                        idWithoutExt, // Giữ nguyên ID
                        ObjectUtils.asMap("invalidate", true, "resource_type", resourceType, "type", "private")
                );

                String status = result.get("result").toString();
                if ("not found".equals(status)) {
                    throw new RuntimeException("Cloudinary: ID not found - " + publicId);
                }
                deleteResults.put(publicId, status);
            } catch (Exception e) {
                deleteResults.put(publicId, "error");
                throw new RuntimeException("Error while deleting " + publicId, e);
            }

        return deleteResults;
    }




}
