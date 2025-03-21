package com.vawndev.spring_boot_readnovel.Services;

import com.vawndev.spring_boot_readnovel.Dto.Requests.ConditionRequest;
import com.vawndev.spring_boot_readnovel.Dto.Requests.Subscription.SubscriptionCreatePlansRequest;
import com.vawndev.spring_boot_readnovel.Dto.Requests.Subscription.SubscriptionPlansRequest;
import com.vawndev.spring_boot_readnovel.Dto.Responses.Subscription.SubscriptionPlansResponse;

import java.util.List;

public interface SubscriptionPlansService {
    List<SubscriptionPlansResponse> getAllSubscriptionPlans();
    void createSubscriptionPlan(SubscriptionCreatePlansRequest req, String bearerToken);
    void removeSubscriptionPlan(ConditionRequest req , String bearerToken);
    void deleteSubscriptionPlan(ConditionRequest req,String bearerToken);
    void updateSubscriptionPlan(SubscriptionPlansRequest req,String bearerToken);
    void editSubscriptionPlan(ConditionRequest req,String bearerToken);
    List<SubscriptionPlansResponse> getAllSubscriptionPlansTrash(String email,String bearerToken);
}
