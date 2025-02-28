package com.vawndev.spring_boot_readnovel.Dto.Responses.Story;


import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StoriesResponse {

    private String id;

    private String title;

    private Long view;

    private double rate;

    private int views;

}
