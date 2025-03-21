package com.vawndev.spring_boot_readnovel.Dto.Requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ConditionRequest {
    @NotNull(message = "id cannot be null")
    @NotBlank(message = "id cannot be blank")
    private String id;

    @NotNull(message = "Email cannot be null")
    @NotBlank(message = "Email cannot be blank")
    private String email;
}
