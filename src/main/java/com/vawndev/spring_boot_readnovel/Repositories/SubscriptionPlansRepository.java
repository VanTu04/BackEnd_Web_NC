package com.vawndev.spring_boot_readnovel.Repositories;

import com.vawndev.spring_boot_readnovel.Entities.SubscriptionPlans;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface SubscriptionPlansRepository extends JpaRepository<SubscriptionPlans, String> {
    List<SubscriptionPlans> findAllByDeleteAtIsNull();
    List<SubscriptionPlans> findAllByDeleteAtIsNotNull();
    SubscriptionPlans findByType(String type);

    Optional<SubscriptionPlans> findByIdAndDeleteAtIsNull( String id);
}
