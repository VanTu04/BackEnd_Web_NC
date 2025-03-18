package com.vawndev.spring_boot_readnovel.Utils;

import com.vawndev.spring_boot_readnovel.Enum.SUBSCRIPTION_TYPE;
import lombok.experimental.UtilityClass;

import java.util.Map;

@UtilityClass
public class SubscriptionUtil {
    public final static Map<SUBSCRIPTION_TYPE, Long> EXPIRATION_DAYS  =Map.of(
            SUBSCRIPTION_TYPE.MAX_VIP, 365L,
            SUBSCRIPTION_TYPE.VIP, 180L,
            SUBSCRIPTION_TYPE.PREMIUM, 90L
    );
}
