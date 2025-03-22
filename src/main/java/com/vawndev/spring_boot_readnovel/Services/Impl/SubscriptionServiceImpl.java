package com.vawndev.spring_boot_readnovel.Services.Impl;

import com.vawndev.spring_boot_readnovel.Dto.Requests.Subscription.SubscriptionRequest;
import com.vawndev.spring_boot_readnovel.Dto.Responses.Subscription.SubscriptionResponse;
import com.vawndev.spring_boot_readnovel.Entities.Subscription;
import com.vawndev.spring_boot_readnovel.Entities.SubscriptionPlans;
import com.vawndev.spring_boot_readnovel.Entities.User;
import com.vawndev.spring_boot_readnovel.Enum.SUBSCRIPTION_TYPE;
import com.vawndev.spring_boot_readnovel.Exceptions.AppException;
import com.vawndev.spring_boot_readnovel.Exceptions.ErrorCode;
import com.vawndev.spring_boot_readnovel.Repositories.SubscriptionPlansRepository;
import com.vawndev.spring_boot_readnovel.Repositories.SubscriptionRepository;
import com.vawndev.spring_boot_readnovel.Repositories.UserRepository;
import com.vawndev.spring_boot_readnovel.Services.SubscriptionService;
import com.vawndev.spring_boot_readnovel.Utils.Help.TokenHelper;
import com.vawndev.spring_boot_readnovel.Utils.JwtUtils;
import com.vawndev.spring_boot_readnovel.Utils.TimeZoneConvert;
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
    private final TokenHelper tokenHelper;
    private final SubscriptionRepository subscriptionRepository;
    private static boolean isPayment=true;
    private final SubscriptionPlansRepository subscriptionPlansRepository;

    @Scheduled(cron = "0 0 0 * * ?") //auto run to check expired of account subscription in 12:00pm every day
    @Transactional
    public void resetExpiredSubscriptions() {
        Instant now = Instant.now();
        Subscription expiredSubscriptions = subscriptionRepository.findByExpiredAtBefore(now).orElseThrow(()->new AppException(ErrorCode.NOT_FOUND));
        if(!expiredSubscriptions.equals(null)){
            subscriptionRepository.delete(expiredSubscriptions);
        }
    }
    @Override
    @Transactional
    public void upgradeSubscription(SubscriptionRequest req, String bearerToken) {
        try {
            User user = tokenHelper.getRealAuthorizedUser(req.getEmail(), bearerToken);

            // Tìm subscription hiện tại của user
            Subscription subscription = subscriptionRepository.findByUserId(user.getId()).orElse(null);

            // Nếu subscription đã tồn tại và chưa hết hạn, trả về thông báo
            if (subscription != null && subscription.getExpiredAt() != null
                    && subscription.getExpiredAt().isAfter(Instant.now())) {
                throw new AppException(ErrorCode.CONFLICT_SUBSCRIPTION);
            }

            
            // Kiểm tra trạng thái thanh toán
            if (!isPayment) {
                throw new AppException(ErrorCode.FAILED_PAYMENT);
            }

            // Nếu chưa có subscription, tạo mới
            if (subscription == null) {
                subscription = new Subscription();
                subscription.setUser(user);
            }

            // Tìm Subscription Plan
            SubscriptionPlans subscriptionPlan = subscriptionPlansRepository.findById(req.getId_plan())
                    .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND));

            // Gán gói mới và lưu
            subscription.setPlan(subscriptionPlan);
            subscriptionRepository.save(subscription);

        } catch (AppException e) {
            throw e; // Nếu là lỗi đã xác định trước, giữ nguyên và trả về
        } catch (Exception e) {
            throw new RuntimeException("Upgrade subscription failed for request: " + req.toString(), e);
        }
    }



    @Override
    public SubscriptionResponse getSubscription(String email, String bearerToken) {
        try {
            User user = tokenHelper.getRealAuthorizedUser(email, bearerToken);
            Subscription subscription = subscriptionRepository.findByUserId(user.getId()).orElse(null);

            if (subscription == null) {
                return SubscriptionResponse.builder()
                        .type(null)
                        .expiredAt(null)
                        .build();
            }

            return SubscriptionResponse.builder()
                    .type(subscription.getPlan().getType())
                    .expiredAt(TimeZoneConvert.convertUtcToUserTimezone(subscription.getExpiredAt()))
                    .expired(subscription.getPlan().getExpired())
                    .build();
        } catch (Exception e) {
            throw new RuntimeException("Failed to get subscription", e);
        }
    }


}
