package com.vawndev.spring_boot_readnovel.Repositories;

import com.vawndev.spring_boot_readnovel.Entities.Story;
import com.vawndev.spring_boot_readnovel.Entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StoryRepository extends JpaRepository<Story, String> {
    Page<Story> findAllByStateAndIsApprovedAndIsAvailableAndDeleteAtIsNull(StoryState state,boolean isApproved, boolean isAvailable ,Pageable pageable);
    Optional<Story> findByIdAndAuthor(String id, User author);
    Page<Story> findAll(Pageable pageable);
    Page<Story> findAllByStateAndIsApprovedAndIsAvailableAndDeleteAtIsNotNull(StoryState state,boolean isApproved, boolean isAvailable,Pageable pageable);
}
