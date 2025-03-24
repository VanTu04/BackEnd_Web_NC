package com.vawndev.spring_boot_readnovel.Dto.Requests.Subscription;

import com.vawndev.spring_boot_readnovel.Dto.Requests.ConditionRequest;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class SubscriptionPlansRequest extends ConditionRequest {
    private String type;
    private Long expired;
    private BigDecimal price;
}
