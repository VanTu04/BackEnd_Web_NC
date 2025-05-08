package com.vawndev.spring_boot_readnovel.Dto.Requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ConditionRequest {
    @NotBlank(message = "id cannot be blank")
    private String id;

}
