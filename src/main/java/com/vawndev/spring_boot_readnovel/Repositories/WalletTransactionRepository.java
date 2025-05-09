package com.vawndev.spring_boot_readnovel.Repositories;

import com.vawndev.spring_boot_readnovel.Entities.User;
import com.vawndev.spring_boot_readnovel.Entities.WalletTransaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;

@Repository
public interface WalletTransactionRepository extends JpaRepository<WalletTransaction, String> {
    WalletTransaction findByUser(User user);
    Page<WalletTransaction> findByUser(User user, Pageable pageable);
    Long countByCreatedAtBetween(Instant start, Instant end);
}
