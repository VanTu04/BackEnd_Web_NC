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

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SubscriptionServiceImpl implements SubscriptionService {
    private final TokenHelper tokenHelper;
    private final SubscriptionRepository subscriptionRepository;
    private static boolean isPayment=true;
    private final SubscriptionPlansRepository subscriptionPlansRepository;
    private final UserRepository userRepository;

    @Scheduled(cron = "0 0 0 * * ?") // Auto run to check expired of account subscription at 12:00 PM every day
    @Transactional
    public void resetExpiredSubscriptions() {
        Instant now = Instant.now();
        List<Subscription> expiredSubscriptions = subscriptionRepository.findByExpiredAtBefore(now);
        if (expiredSubscriptions.isEmpty()) {
            return;
        }
        subscriptionRepository.deleteAll(expiredSubscriptions);
    }

    @Override
    @Transactional
    public void upgradeSubscription(SubscriptionRequest req, String bearerToken) {
        try {
            // Lấy thông tin người dùng từ token
            User user = tokenHelper.getRealAuthorizedUser(req.getEmail(), bearerToken);

            // Tìm subscription hiện tại của user
            Subscription subscription = subscriptionRepository.findByUserId(user.getId()).orElse(null);

            // Nếu subscription đã tồn tại và chưa hết hạn, trả về thông báo
            if (subscription != null && subscription.getExpiredAt() != null
                    && subscription.getExpiredAt().isAfter(Instant.now())) {
                throw new AppException(ErrorCode.CONFLICT_SUBSCRIPTION);
            }

            // Kiểm tra số dư của người dùng
            BigDecimal balance = Optional.ofNullable(user.getBalance()).orElse(BigDecimal.ZERO);

            // Tìm Subscription Plan
            SubscriptionPlans subscriptionPlan = subscriptionPlansRepository.findById(req.getId_plan())
                    .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND));

            // Kiểm tra xem người dùng có đủ số dư thanh toán không
            if (balance.compareTo(subscriptionPlan.getPrice()) < 0) {
                ErrorCode errorCode=ErrorCode.FAILED_PAYMENT;
                errorCode.getMessage("Your balance is too low");
                throw new AppException(ErrorCode.FAILED_PAYMENT);
            }

            // Nếu chưa có subscription, tạo mới
            if (subscription == null) {
                subscription = new Subscription();
                subscription.setUser(user);
            }

            // Gán gói mới và trừ tiền trong tài khoản người dùng
            subscription.setPlan(subscriptionPlan);
            BigDecimal newBalance = balance.subtract(subscriptionPlan.getPrice());

            // Đảm bảo số dư không âm
            if (newBalance.compareTo(BigDecimal.ZERO) < 0) {
                throw new AppException(ErrorCode.FAILED_PAYMENT);
            }

            user.setBalance(newBalance);
            user.setSubscription(subscription);
            userRepository.save(user);
            subscriptionRepository.save(subscription);

        } catch (AppException e) {
            throw e; // Nếu là lỗi đã xác định trước, giữ nguyên và trả về
        } catch (Exception e) {
            // Ném ra lỗi với thông tin chi tiết
            throw new AppException(ErrorCode.SERVER_ERROR);
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
