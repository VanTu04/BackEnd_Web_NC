package com.vawndev.spring_boot_readnovel.Dto.Responses.Story;


import com.vawndev.spring_boot_readnovel.Dto.Responses.Chapter.ChapterResponses;
import lombok.*;

import java.util.List;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StoryDetailResponses {
    private StoryResponse story;
    private List<ChapterResponses> chapter ;
}
