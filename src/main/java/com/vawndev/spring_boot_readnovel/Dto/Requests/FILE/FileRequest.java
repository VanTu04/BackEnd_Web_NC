package com.vawndev.spring_boot_readnovel.Dto.Requests.FILE;

import com.vawndev.spring_boot_readnovel.Enum.RESOURCE_TYPE;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
public abstract class FileRequest {
    @NotNull(message = "file must be not null")
    private List<MultipartFile> file;
    @NotNull(message = "type of must be not null")
    public abstract RESOURCE_TYPE Type();
}
