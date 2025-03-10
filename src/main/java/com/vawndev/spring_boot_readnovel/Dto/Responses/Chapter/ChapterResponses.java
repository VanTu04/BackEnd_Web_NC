package com.vawndev.spring_boot_readnovel.Dto.Responses.Chapter;

import com.vawndev.spring_boot_readnovel.Dto.Responses.ImageResponse;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChapterResponses {
    private String title;

    private String content;

    private BigDecimal price;

    private String signature;

    private String image_proxy;

    private String timeStamp;

    private long expiredAt;
}
