package com.vawndev.spring_boot_readnovel.Dto.Responses.Subscription;

import com.vawndev.spring_boot_readnovel.Entities.SubscriptionPlans;
import com.vawndev.spring_boot_readnovel.Enum.SUBSCRIPTION_TYPE;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@AllArgsConstructor
@Builder
public class SubscriptionResponse {
    private String type;
    private Long expired;
    private String expiredAt;
}
