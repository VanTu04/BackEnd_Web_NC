package com.vawndev.spring_boot_readnovel.Dto.Responses.Subscription;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class SubscriptionPlansResponse {
    private String id;
    private String type;
    private Long expired;
}
