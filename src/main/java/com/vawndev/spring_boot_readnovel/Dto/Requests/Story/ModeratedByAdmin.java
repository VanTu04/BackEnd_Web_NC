package com.vawndev.spring_boot_readnovel.Dto.Requests.Story;


import com.vawndev.spring_boot_readnovel.Enum.IS_AVAILBLE;
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
    private IS_AVAILBLE isAvailable ;
}
