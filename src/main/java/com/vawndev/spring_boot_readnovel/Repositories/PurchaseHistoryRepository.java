package com.vawndev.spring_boot_readnovel.Repositories;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.vawndev.spring_boot_readnovel.Entities.PurchaseHistory;

public interface PurchaseHistoryRepository extends JpaRepository<PurchaseHistory, String> {

    @Query("SELECT pc FROM PurchaseHistory pc WHERE pc.chapter.id =:chapter_id AND pc.user.id =:user_id")
    Optional<PurchaseHistory> findByChapterAndUser(String chapter_id, String user_id);

}
