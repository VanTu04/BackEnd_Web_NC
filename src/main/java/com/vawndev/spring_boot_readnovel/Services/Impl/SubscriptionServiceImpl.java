package com.vawndev.spring_boot_readnovel.Services.Impl;

import com.vawndev.spring_boot_readnovel.Entities.Subscription;
import com.vawndev.spring_boot_readnovel.Entities.User;
import com.vawndev.spring_boot_readnovel.Enum.SUBSCRIPTION_TYPE;
import com.vawndev.spring_boot_readnovel.Exceptions.AppException;
import com.vawndev.spring_boot_readnovel.Exceptions.ErrorCode;
import com.vawndev.spring_boot_readnovel.Repositories.SubscriptionRepository;
import com.vawndev.spring_boot_readnovel.Repositories.UserRepository;
import com.vawndev.spring_boot_readnovel.Services.SubscriptionService;
import com.vawndev.spring_boot_readnovel.Utils.Help.TokenHelper;
import com.vawndev.spring_boot_readnovel.Utils.JwtUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.hc.client5.http.auth.BearerToken;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SubscriptionServiceImpl implements SubscriptionService {
    private final UserRepository userRepository;
    private final JwtUtils jwtUtils;
    private final TokenHelper tokenHelper;
    private final SubscriptionRepository subscriptionRepository;
    private static boolean isPayment=true;

    @Scheduled(cron = "0 0 0 * * ?") //auo run for check expired of account subscription in 12:00pm every day
    @Transactional
    public void resetExpiredSubscriptions() {
        Instant now = Instant.now();
        Subscription expiredSubscriptions = subscriptionRepository.findByExpiredAtBefore(now).orElseThrow(()->new AppException(ErrorCode.NOT_FOUND));
        expiredSubscriptions.setType(SUBSCRIPTION_TYPE.REGULAR);
        expiredSubscriptions.setExpiredAt(null);
        subscriptionRepository.save(expiredSubscriptions);
    }
    @Override
    public void upgradeSubscription(SUBSCRIPTION_TYPE type, String bearerToken) {
        User user = jwtUtils.validToken(tokenHelper.getTokenInfo(bearerToken));
        Subscription subscription = subscriptionRepository.findById(user.getId())
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND));

        if(isPayment){
            subscription.setType(type);
        }
    }

}
