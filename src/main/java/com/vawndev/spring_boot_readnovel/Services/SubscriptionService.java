package com.vawndev.spring_boot_readnovel.Services;

import com.vawndev.spring_boot_readnovel.Dto.Requests.Subscription.SubscriptionRequest;
import com.vawndev.spring_boot_readnovel.Dto.Responses.Subscription.SubscriptionResponse;
import com.vawndev.spring_boot_readnovel.Entities.Subscription;
import com.vawndev.spring_boot_readnovel.Enum.SUBSCRIPTION_TYPE;

import java.util.List;

public interface SubscriptionService {
    void upgradeSubscription(SubscriptionRequest req);
    void upgradeRole();
    SubscriptionResponse getSubscription();

}
