package com.vawndev.spring_boot_readnovel.Repositories;

import com.vawndev.spring_boot_readnovel.Entities.User;
import com.vawndev.spring_boot_readnovel.Entities.WithdrawTransaction;
import com.vawndev.spring_boot_readnovel.Enum.TransactionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.Optional;

public interface WithdrawTransactionRepository extends JpaRepository<WithdrawTransaction,String> {
    Optional<WithdrawTransaction> findByIdAndUser(String id, User user);

    @Query("SELECT w FROM WithdrawTransaction w WHERE w.user = :user AND w.status = :status")
    Page<WithdrawTransaction> findAllByUserAndStatus(User user, TransactionStatus status, Pageable pageable);

    @Modifying
    @Query("DELETE FROM WithdrawTransaction w WHERE w.deleteAt IS NOT NULL AND w.deleteAt <= :expiredTime")
    void deleteOldWithdrawTransactions(Instant expiredTime);
}
