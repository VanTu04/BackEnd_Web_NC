package com.vawndev.spring_boot_readnovel.Dto.Requests.Chapter;

import com.vawndev.spring_boot_readnovel.Dto.Requests.FILE.FileRequest;
import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ChapterUploadRequest {
    private FileRequest file;
    private ChapterRequest chapter;
}
