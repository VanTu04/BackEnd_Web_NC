package com.vawndev.spring_boot_readnovel.Dto.Requests.Story;


import lombok.*;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StoryCondition {
    private boolean isApproved;
    private boolean isArchived;
}
