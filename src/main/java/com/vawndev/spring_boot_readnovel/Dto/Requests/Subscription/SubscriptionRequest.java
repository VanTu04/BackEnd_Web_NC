package com.vawndev.spring_boot_readnovel.Dto.Requests.Subscription;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SubscriptionRequest {
    @NotBlank(message = "id plan must not be blank")
    private String id_plan;


}
