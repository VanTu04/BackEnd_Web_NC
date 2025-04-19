package com.vawndev.spring_boot_readnovel.Dto.Responses.Chapter;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.vawndev.spring_boot_readnovel.Dto.Responses.FileResponse;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class ChapterResponseDetail extends ChapterResponsePurchase {
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<FileResponse> files;

}
