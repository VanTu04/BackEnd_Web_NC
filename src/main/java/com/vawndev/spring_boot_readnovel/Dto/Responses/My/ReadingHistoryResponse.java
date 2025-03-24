package com.vawndev.spring_boot_readnovel.Dto.Responses.My;

import com.vawndev.spring_boot_readnovel.Dto.Responses.Chapter.ChapterResponsePurchase;
import com.vawndev.spring_boot_readnovel.Dto.Responses.Chapter.ChaptersResponse;
import com.vawndev.spring_boot_readnovel.Dto.Responses.Story.StoriesResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReadingHistoryResponse {
    private StoriesResponse story;
    private List<ChaptersResponse> chapters;
}
