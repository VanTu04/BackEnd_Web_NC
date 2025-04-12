package com.vawndev.spring_boot_readnovel.Dto.Responses.Story;


import com.vawndev.spring_boot_readnovel.Dto.Responses.Chapter.ChapterResponseDetail;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class StoryDetailResponses extends StoryResponse {
    private List<ChapterResponseDetail> chapter ;
}
