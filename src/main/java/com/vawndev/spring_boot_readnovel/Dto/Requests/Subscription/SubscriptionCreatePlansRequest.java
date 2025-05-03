package com.vawndev.spring_boot_readnovel.Dto.Requests.Subscription;

import com.vawndev.spring_boot_readnovel.Dto.Requests.ConditionRequest;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class SubscriptionCreatePlansRequest {
    @NotNull(message = "Type cannot be null")
    @NotBlank(message = "Type cannot be blank")
    private String type;

    @NotNull(message = "Price cannot be null")
    private BigDecimal price;

    @NotNull(message = "Expired time cannot be null")
    private Long expired;
}
