package com.vawndev.spring_boot_readnovel.Services.Impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.vawndev.spring_boot_readnovel.Dto.Requests.FileRequest;
import com.vawndev.spring_boot_readnovel.Dto.Responses.CloudinaryResponse;
import com.vawndev.spring_boot_readnovel.Services.CloundService;
import com.vawndev.spring_boot_readnovel.Util.FileUpload;
import jakarta.transaction.Transactional;
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
            String type=req.getType().toString().toLowerCase();
            FileUpload.assertAllowed(file, ".*\\.(jpg|jpeg|png)$");
            FileUpload.getType(req.getType());
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




}
