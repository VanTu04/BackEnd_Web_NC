package com.vawndev.spring_boot_readnovel.Repositories;

import com.vawndev.spring_boot_readnovel.Entities.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface SubscriptionRepository extends JpaRepository<Subscription, String> {
    Optional<Subscription> findByUserId(String userId);
    List<Subscription> findByExpiredAtBefore(Instant now);
}
