package com.vawndev.spring_boot_readnovel.Dto.Requests.FILE;

import com.vawndev.spring_boot_readnovel.Enum.RESOURCE_TYPE;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class ImageCoverRequest {
    @NotNull(message = "image of must be not null")
    private MultipartFile image_cover;
    @NotNull(message = "type of must be not null")

    public RESOURCE_TYPE Type(){
        return RESOURCE_TYPE.IMAGE;
    }
}
