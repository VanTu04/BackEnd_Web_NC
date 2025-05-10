package com.vawndev.spring_boot_readnovel.Repositories;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.vawndev.spring_boot_readnovel.Entities.PurchaseHistory;

public interface PurChaseRepository extends JpaRepository<PurchaseHistory, String> {

    @Query("SELECT p FROM PurchaseHistory p WHERE p.user.id = :id")
    List<PurchaseHistory> getAllByUserId(@Param("id") String id, Pageable pageable);
}
