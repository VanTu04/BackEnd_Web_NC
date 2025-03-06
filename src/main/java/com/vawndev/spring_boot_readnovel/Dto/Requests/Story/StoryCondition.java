package com.vawndev.spring_boot_readnovel.Dto.Requests.Story;


import com.vawndev.spring_boot_readnovel.Enum.StoryState;
import lombok.*;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StoryCondition {
    private StoryState state;
    private boolean isApproved;
    private boolean isArchived;
}
