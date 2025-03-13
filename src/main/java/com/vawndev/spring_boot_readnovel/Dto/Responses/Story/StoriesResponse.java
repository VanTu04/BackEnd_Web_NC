package com.vawndev.spring_boot_readnovel.Dto.Responses.Story;


import com.vawndev.spring_boot_readnovel.Enum.StoryType;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StoriesResponse {

    private String id;

    private String title;

    private StoryType type;

    private Long view;

    private double rate;

    private int views;

    private String coverImage;

}
