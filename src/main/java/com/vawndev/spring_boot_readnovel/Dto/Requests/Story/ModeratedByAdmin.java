package com.vawndev.spring_boot_readnovel.Dto.Requests.Story;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class ModeratedByAdmin {
    private String story_id;
    private String email;
    private Boolean isAvailable ;
}
