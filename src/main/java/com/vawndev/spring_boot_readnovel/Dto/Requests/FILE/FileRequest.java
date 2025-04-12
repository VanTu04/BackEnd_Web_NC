package com.vawndev.spring_boot_readnovel.Dto.Requests.FILE;

import com.vawndev.spring_boot_readnovel.Enum.RESOURCE_TYPE;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
public abstract class FileRequest {
    private List<MultipartFile> file;
    public abstract RESOURCE_TYPE Type();
}
