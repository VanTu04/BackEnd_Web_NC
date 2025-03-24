package com.vawndev.spring_boot_readnovel.Dto.Requests.Story;


import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StoryCondition {
    @NotBlank(message = "id cannot be blank")

    private String id;
    @NotBlank(message = "email cannot be blank")

    private String email;
}
