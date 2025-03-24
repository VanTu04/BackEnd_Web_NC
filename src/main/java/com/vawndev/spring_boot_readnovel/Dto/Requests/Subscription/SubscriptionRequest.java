package com.vawndev.spring_boot_readnovel.Dto.Requests.Subscription;

import com.vawndev.spring_boot_readnovel.Enum.SUBSCRIPTION_TYPE;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SubscriptionRequest {
    @NotBlank(message = "email must not be blank")
    private String email;
    @NotBlank(message = "id plan must not be blank")
    private String id_plan;


}
