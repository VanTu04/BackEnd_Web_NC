package com.vawndev.spring_boot_readnovel.Dto.Requests.Story;


import com.vawndev.spring_boot_readnovel.Enum.IS_AVAILBLE;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class ModeratedByAdmin {
    @NotNull(message = "id cannot be null")
    @NotBlank(message = "id cannot be blank")
    private String story_id;
    @NotNull(message = "email cannot be null")
    @NotBlank(message = "email cannot be blank")
    private String email;
    @NotNull(message = "availble cannot be null")
    @NotBlank(message = "availble cannot be blank")
    private IS_AVAILBLE isAvailable ;
}
