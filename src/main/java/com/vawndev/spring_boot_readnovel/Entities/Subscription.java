package com.vawndev.spring_boot_readnovel.Entities;

import com.vawndev.spring_boot_readnovel.Enum.SUBSCRIPTION_TYPE;
import com.vawndev.spring_boot_readnovel.Enum.TransactionType;
import com.vawndev.spring_boot_readnovel.Utils.SubscriptionUtil;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Subscription extends BaseEntity {
    @OneToOne
    @JoinColumn(name = "user_id", unique = true)
    private User user;

    @ManyToOne
    @JoinColumn(name = "plan_id")
    private SubscriptionPlans plan;

    private Instant expiredAt =null;

    @PrePersist
    @PreUpdate
    private void updateExpiredAt() {
        if (plan != null) {
            expiredAt = Instant.now().plus(plan.getExpired(), ChronoUnit.DAYS);
        }
    }
}
