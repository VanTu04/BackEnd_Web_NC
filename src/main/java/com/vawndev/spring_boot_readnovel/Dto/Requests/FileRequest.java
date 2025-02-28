package com.vawndev.spring_boot_readnovel.Dto.Requests;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
public class FileRequest {
    private List<MultipartFile> file;
    private String type;
}
