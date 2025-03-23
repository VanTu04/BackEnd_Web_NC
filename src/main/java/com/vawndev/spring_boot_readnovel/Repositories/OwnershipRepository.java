package com.vawndev.spring_boot_readnovel.Repositories;

import com.vawndev.spring_boot_readnovel.Entities.Chapter;
import com.vawndev.spring_boot_readnovel.Entities.Ownership;
import com.vawndev.spring_boot_readnovel.Entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface OwnershipRepository extends JpaRepository<Ownership, UUID> {
    boolean existsByUserAndChapter(User user, Chapter chapter);
}
