package com.vawndev.spring_boot_readnovel.Entities;

import com.vawndev.spring_boot_readnovel.Enum.SUBSCRIPTION_TYPE;
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

    @Enumerated(EnumType.STRING)
    private SUBSCRIPTION_TYPE type = SUBSCRIPTION_TYPE.REGULAR;

    private Instant expiredAt;

    @PrePersist
    @PreUpdate
    private void updateExpiredAt() {
        if (type == SUBSCRIPTION_TYPE.REGULAR) {
            expiredAt = null;
        } else {
            expiredAt = Instant.now().plus(SubscriptionUtil.EXPIRATION_DAYS.getOrDefault(type, 0L), ChronoUnit.DAYS);
        }
    }
}
