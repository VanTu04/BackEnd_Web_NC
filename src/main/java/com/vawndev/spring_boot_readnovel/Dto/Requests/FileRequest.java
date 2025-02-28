package com.vawndev.spring_boot_readnovel.Dto.Requests;

import com.vawndev.spring_boot_readnovel.Enum.RESOURCE_TYPE;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
public class FileRequest {
    private List<MultipartFile> file;
    private RESOURCE_TYPE type;
}
