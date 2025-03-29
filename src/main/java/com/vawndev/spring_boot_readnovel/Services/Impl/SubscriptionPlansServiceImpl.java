package com.vawndev.spring_boot_readnovel.Services.Impl;

import com.vawndev.spring_boot_readnovel.Dto.Requests.ConditionRequest;
import com.vawndev.spring_boot_readnovel.Dto.Requests.Subscription.SubscriptionCreatePlansRequest;
import com.vawndev.spring_boot_readnovel.Dto.Requests.Subscription.SubscriptionPlansRequest;
import com.vawndev.spring_boot_readnovel.Dto.Responses.Subscription.SubscriptionPlansResponse;
import com.vawndev.spring_boot_readnovel.Entities.SubscriptionPlans;
import com.vawndev.spring_boot_readnovel.Entities.User;
import com.vawndev.spring_boot_readnovel.Exceptions.AppException;
import com.vawndev.spring_boot_readnovel.Exceptions.ErrorCode;
import com.vawndev.spring_boot_readnovel.Repositories.SubscriptionPlansRepository;
import com.vawndev.spring_boot_readnovel.Services.SubscriptionPlansService;
import com.vawndev.spring_boot_readnovel.Utils.Help.TokenHelper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SubscriptionPlansServiceImpl implements SubscriptionPlansService {
    private final SubscriptionPlansRepository subscriptionPlansRepository;
    private final TokenHelper tokenHelper;

    @Override
    public List<SubscriptionPlansResponse> getAllSubscriptionPlans() {
        List<SubscriptionPlans> subscriptionPlans = subscriptionPlansRepository.findAllByDeleteAtIsNull();
        return subscriptionPlans.stream().map(sub ->
                SubscriptionPlansResponse.builder()
                        .id(sub.getId())
                        .type(sub.getType())
                        .expired(sub.getExpired())
                        .price(sub.getPrice())
                        .build()
        ).collect(Collectors.toList());
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public void createSubscriptionPlan(SubscriptionCreatePlansRequest req) {
        tokenHelper.getUserO2Auth();
        SubscriptionPlans subscriptionPlans = subscriptionPlansRepository.findByType(req.getType());
        if (subscriptionPlans != null) {
            throw new AppException(ErrorCode.CONFLICT);
        }
        subscriptionPlans = SubscriptionPlans.builder()
                .expired(req.getExpired())
                .type(req.getType())
                .build();
        subscriptionPlansRepository.save(subscriptionPlans);
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public void removeSubscriptionPlan(String id_plan) {
        tokenHelper.getUserO2Auth();

        Optional<SubscriptionPlans> optionalPlan = subscriptionPlansRepository.findById(id_plan);
        if (optionalPlan.isPresent()) {
            SubscriptionPlans plan = optionalPlan.get();
            plan.setDeleteAt(Instant.now());
            subscriptionPlansRepository.save(plan);
        } else {
            throw new AppException(ErrorCode.OBJECT_NOT_FOUND);
        }
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteSubscriptionPlan(String id_plan) {
        tokenHelper.getUserO2Auth();
        Optional<SubscriptionPlans> optionalPlan = subscriptionPlansRepository.findById(id_plan);
        if (optionalPlan.isPresent()) {
            subscriptionPlansRepository.deleteById(id_plan);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Subscription plan not found");
        }
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public void updateSubscriptionPlan(SubscriptionPlansRequest req,String id_plan) {
        tokenHelper.getUserO2Auth();
        Optional<SubscriptionPlans> optionalPlan = subscriptionPlansRepository.findByIdAndDeleteAtIsNull(id_plan);
        if (optionalPlan.isPresent()) {
            SubscriptionPlans plan = optionalPlan.get();
            plan.setExpired(req.getExpired());
            plan.setType(req.getType());
            plan.setPrice(req.getPrice());
            subscriptionPlansRepository.save(plan);
        } else {
            throw new AppException(ErrorCode.OBJECT_NOT_FOUND);
        }
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public void editSubscriptionPlan(ConditionRequest req, String id_plan) {
        tokenHelper.getUserO2Auth();
        Optional<SubscriptionPlans> optionalPlan = subscriptionPlansRepository.findById(id_plan);
        if (optionalPlan.isPresent()) {
            SubscriptionPlans plan = optionalPlan.get();
            plan.setDeleteAt(null);
            subscriptionPlansRepository.save(plan);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Subscription plan not found");
        }
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public List<SubscriptionPlansResponse> getAllSubscriptionPlansTrash() {
        tokenHelper.getUserO2Auth();
        List<SubscriptionPlans> subscriptionPlans = subscriptionPlansRepository.findAllByDeleteAtIsNotNull();
        return subscriptionPlans.stream().map(sub ->
                SubscriptionPlansResponse.builder()
                        .id(sub.getId())
                        .type(sub.getType())
                        .expired(sub.getExpired())
                        .build()
        ).collect(Collectors.toList());
    }
}
