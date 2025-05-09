package com.vawndev.spring_boot_readnovel.Dto.Responses.Subscription;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class PlansResponse {
    private List<SubscriptionPlansResponse> plans;
    private RolePlanRespone author_role;
}
