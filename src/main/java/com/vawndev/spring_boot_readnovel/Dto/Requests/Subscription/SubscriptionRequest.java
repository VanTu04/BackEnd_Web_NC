package com.vawndev.spring_boot_readnovel.Dto.Requests.Subscription;

import com.vawndev.spring_boot_readnovel.Enum.SUBSCRIPTION_TYPE;
import lombok.Data;

@Data
public class SubscriptionRequest {
    private String email;
    private String id_plan;

}
