package com.vawndev.spring_boot_readnovel.Services;

import com.vawndev.spring_boot_readnovel.Entities.Subscription;
import com.vawndev.spring_boot_readnovel.Enum.SUBSCRIPTION_TYPE;

public interface SubscriptionService {
    void upgradeSubscription(SUBSCRIPTION_TYPE type, String bearerToken);
}
