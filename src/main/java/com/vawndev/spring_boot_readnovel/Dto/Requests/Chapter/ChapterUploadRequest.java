package com.vawndev.spring_boot_readnovel.Dto.Requests.Chapter;

import com.vawndev.spring_boot_readnovel.Dto.Requests.FILE.FileRequest;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ChapterUploadRequest {
    @NotNull(message = "file must not be null")
    private FileRequest file;
    @NotNull(message = "chapter must not be null")
    private ChapterRequest chapter;
}
