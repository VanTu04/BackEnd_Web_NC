package com.vawndev.spring_boot_readnovel.Dto.Requests.Chapter;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;


import java.math.BigDecimal;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
public class ChapterRequest {

    @NotBlank(message = "id story must be not blank")
    private String story_id;
    @NotBlank(message = "id cotent must be not blank")
    private String content;
    @NotNull(message = "price must be not blank")
    private BigDecimal price;

}
