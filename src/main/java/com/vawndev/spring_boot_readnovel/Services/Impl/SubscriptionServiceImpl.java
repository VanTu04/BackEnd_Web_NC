package com.vawndev.spring_boot_readnovel.Services.Impl;

import com.vawndev.spring_boot_readnovel.Dto.Requests.Subscription.SubscriptionRequest;
import com.vawndev.spring_boot_readnovel.Dto.Responses.Subscription.SubscriptionResponse;
import com.vawndev.spring_boot_readnovel.Entities.*;
import com.vawndev.spring_boot_readnovel.Enum.SUBSCRIPTION_TYPE;
import com.vawndev.spring_boot_readnovel.Enum.TransactionStatus;
import com.vawndev.spring_boot_readnovel.Enum.TransactionType;
import com.vawndev.spring_boot_readnovel.Exceptions.AppException;
import com.vawndev.spring_boot_readnovel.Exceptions.ErrorCode;
import com.vawndev.spring_boot_readnovel.Repositories.*;
import com.vawndev.spring_boot_readnovel.Services.SubscriptionService;
import com.vawndev.spring_boot_readnovel.Utils.Help.TokenHelper;
import com.vawndev.spring_boot_readnovel.Utils.JwtUtils;
import com.vawndev.spring_boot_readnovel.Utils.TimeZoneConvert;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.hc.client5.http.auth.BearerToken;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class SubscriptionServiceImpl implements SubscriptionService {
    private final TokenHelper tokenHelper;
    private final SubscriptionRepository subscriptionRepository ;
    private final SubscriptionPlansRepository subscriptionPlansRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final WalletTransactionRepository transactionRepository;
    private final JwtUtils jwtUtils;
    private User getAuthenticatedUser() {
        return tokenHelper.getUserO2Auth();
    }


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
    @PreAuthorize("hasAuthority('AUTHOR') or hasAuthority('USER')")
    public void upgradeSubscription(SubscriptionRequest req) {
        User user = getAuthenticatedUser();
        Subscription subscription = subscriptionRepository.findByUserId(user.getId()).orElse(null);

        // ✅ Kiểm tra nếu user đã có subscription còn hạn
        if (subscription != null && subscription.getExpiredAt() != null
                && subscription.getExpiredAt().isAfter(Instant.now())) {
            throw new AppException(ErrorCode.CONFLICT_SUBSCRIPTION);
        }

        // ✅ Kiểm tra số dư
        BigDecimal balance = Optional.ofNullable(user.getBalance()).orElse(BigDecimal.ZERO);
        SubscriptionPlans subscriptionPlan = subscriptionPlansRepository.findById(req.getId_plan())
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND));

        if (balance.compareTo(subscriptionPlan.getPrice()) < 0) {
            throw new AppException(ErrorCode.FAILED_PAYMENT);
        }

        // ✅ Nếu chưa có subscription, tạo mới
        if (subscription == null) {
            subscription = new Subscription();
            subscription.setUser(user);
        }

        subscription.setPlan(subscriptionPlan);
        BigDecimal newBalance = balance.subtract(subscriptionPlan.getPrice());

        if (newBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new AppException(ErrorCode.FAILED_PAYMENT);
        }

        // ✅ Lưu thông tin mới vào database
        user.setBalance(newBalance);
        user.setSubscription(subscription);
        userRepository.save(user);
        subscriptionRepository.save(subscription);
    }


    @Override
    @PreAuthorize("hasAuthority('AUTHOR') or hasAuthority('USER')")
        public SubscriptionResponse getSubscription() {
            User user = getAuthenticatedUser();
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
    }
    @Override
    @Transactional
    @PreAuthorize("hasRole('USER')")
        public void upgradeRole() {
        User user =getAuthenticatedUser();

        user.getRoles().forEach(role -> {
            if (role.getName().equals("AUTHOR")) {
                throw new AppException(ErrorCode.CONFLICT,"This role");
            }
        });

        Role newRole = roleRepository.getRoles().stream()
                .filter(role->role.getName().equals("AUTHOR"))
                .findFirst().orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND,"AUTHOR"));

        BigDecimal balance = Optional.ofNullable(user.getBalance()).orElse(BigDecimal.ZERO);

        if (balance.compareTo(newRole.getPrice()) < 0) {
            throw new AppException(ErrorCode.FAILED_PAYMENT);
        }

        user.setBalance(balance.subtract(newRole.getPrice()));
        user.getRoles().add(newRole);
        userRepository.save(user);
    }



}
