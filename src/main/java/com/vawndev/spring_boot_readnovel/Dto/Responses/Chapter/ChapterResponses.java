package com.vawndev.spring_boot_readnovel.Dto.Responses.Chapter;

import com.vawndev.spring_boot_readnovel.Entities.Image;
import com.vawndev.spring_boot_readnovel.Entities.Story;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
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

    private List<Image> images;
}
