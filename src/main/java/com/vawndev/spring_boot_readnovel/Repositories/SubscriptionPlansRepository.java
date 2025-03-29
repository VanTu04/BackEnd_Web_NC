package com.vawndev.spring_boot_readnovel.Repositories;

import com.vawndev.spring_boot_readnovel.Entities.SubscriptionPlans;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SubscriptionPlansRepository extends JpaRepository<SubscriptionPlans, String> {
    List<SubscriptionPlans> findAllByDeleteAtIsNull();
    List<SubscriptionPlans> findAllByDeleteAtIsNotNull();
    SubscriptionPlans findByType(String type);
    Optional<SubscriptionPlans> findByIdAndDeleteAtIsNull( String id);
}
