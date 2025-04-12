package com.vawndev.spring_boot_readnovel.Services;

import com.vawndev.spring_boot_readnovel.Dto.Requests.ConditionRequest;
import com.vawndev.spring_boot_readnovel.Dto.Requests.Subscription.SubscriptionCreatePlansRequest;
import com.vawndev.spring_boot_readnovel.Dto.Requests.Subscription.SubscriptionPlansRequest;
import com.vawndev.spring_boot_readnovel.Dto.Responses.Subscription.SubscriptionPlansResponse;

import java.util.List;

public interface SubscriptionPlansService {
    List<SubscriptionPlansResponse> getAllSubscriptionPlans();
    void createSubscriptionPlan(SubscriptionCreatePlansRequest req);
    void removeSubscriptionPlan(String id_plan);
    void deleteSubscriptionPlan(String id_plan);
    void updateSubscriptionPlan(SubscriptionPlansRequest req,String id_plan);
    void editSubscriptionPlan(ConditionRequest req, String id_plan);
    List<SubscriptionPlansResponse> getAllSubscriptionPlansTrash();
}
