package com.vawndev.spring_boot_readnovel.Services.Impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.vawndev.spring_boot_readnovel.Dto.Requests.FileRequest;
import com.vawndev.spring_boot_readnovel.Dto.Responses.CloudinaryResponse;
import com.vawndev.spring_boot_readnovel.Services.CloundService;
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
            Map<String, Object> uploadResult = cloudinary.uploader().upload(file.getBytes(),
                    ObjectUtils.asMap(
                            "resource_type", req.getType(),
                            "folder", req.getType()
                    ));
            String fileUrl = uploadResult.get("url").toString();
            fileUrls.add(fileUrl);
        }
        return fileUrls;
    }


}
