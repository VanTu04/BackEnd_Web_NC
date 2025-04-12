package com.vawndev.spring_boot_readnovel.Dto.Requests.Category;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CategoryRequests {
    @NotBlank(message = "id category must be not blank")
    private String id;
    @NotBlank(message = "id category must be not blank")
    private String name;
}
