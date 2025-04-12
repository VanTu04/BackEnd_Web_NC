package com.vawndev.spring_boot_readnovel.Dto.Requests.FILE;

import com.vawndev.spring_boot_readnovel.Enum.RESOURCE_TYPE;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class ImageCoverRequest {
    private MultipartFile image_cover;
    public RESOURCE_TYPE Type(){
        return RESOURCE_TYPE.IMAGE;
    }
}
