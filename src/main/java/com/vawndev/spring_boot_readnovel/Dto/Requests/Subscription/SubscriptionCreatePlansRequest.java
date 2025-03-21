package com.vawndev.spring_boot_readnovel.Dto.Requests.Subscription;

import com.vawndev.spring_boot_readnovel.Dto.Requests.ConditionRequest;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SubscriptionCreatePlansRequest  {
    @NotNull(message = "Email cannot be null")
    @NotBlank(message = "Email cannot be blank")
    private String email;

    @NotNull(message = "Type cannot be null")
    @NotBlank(message = "Type cannot be blank")
    private String type;

    @NotNull(message = "Expired time cannot be null")
    private Long expired;
}
