package com.vawndev.spring_boot_readnovel.Services.Impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.vawndev.spring_boot_readnovel.Dto.Requests.FILE.FileRequest;
import com.vawndev.spring_boot_readnovel.Dto.Requests.FILE.ImageCoverRequest;
import com.vawndev.spring_boot_readnovel.Services.CloundService;
import com.vawndev.spring_boot_readnovel.Util.FileUpload;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CloundServiceImpl implements CloundService {

    private final Cloudinary cloudinary;

    @Override
    public List<String> getUrlAfterUpload(FileRequest req) throws IOException {
        List<String> fileUrls = new ArrayList<>();

        for (MultipartFile file : req.getFile()) {
            FileUpload.assertAllowed(file, ".*\\.(jpg|jpeg|png|docx)$");
            String type= FileUpload.getType(req.Type());

            Map<String, Object> uploadResult = cloudinary.uploader().upload(file.getBytes(),
                    ObjectUtils.asMap(
                            "resource_type",type,
                            "folder", type
                    ));

            String fileUrl = uploadResult.get("url").toString();
            fileUrls.add(fileUrl);
        }
        return fileUrls;
    }

    @Override
    public String getUrlCoverAfterUpload(ImageCoverRequest cover) throws IOException {
        FileUpload.assertAllowed(cover.getImage_cover(), ".*\\.(jpg|jpeg|png)$");
        String type= FileUpload.getType(cover.Type());

        Map<String, Object> uploadResult = cloudinary.uploader().upload(cover.getImage_cover().getBytes(),
                ObjectUtils.asMap(
                        "resource_type",type,
                        "folder", type
                ));

        String fileUrl = uploadResult.get("url").toString();
        return fileUrl;
    }


}
