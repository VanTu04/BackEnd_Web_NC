package com.vawndev.spring_boot_readnovel.Repositories;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.vawndev.spring_boot_readnovel.Entities.Story;
import com.vawndev.spring_boot_readnovel.Entities.User;

@Repository
public interface StoryRepository extends JpaRepository<Story, String> {
    Page<Story> findAllByStateAndIsApprovedAndIsAvailableAndDeleteAtIsNull(double rate,boolean isApproved, boolean isAvailable ,Pageable pageable);
    Optional<Story> findByIdAndAuthor(String id, User author);
    Page<Story> findAll(Pageable pageable);
    Page<Story> findAllByStateAndIsApprovedAndIsAvailableAndDeleteAtIsNotNull(double rate,boolean isApproved, boolean isAvailable,Pageable pageable);
}
