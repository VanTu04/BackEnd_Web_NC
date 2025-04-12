package com.vawndev.spring_boot_readnovel.Repositories;

import com.vawndev.spring_boot_readnovel.Entities.FavoriteStory;
import com.vawndev.spring_boot_readnovel.Entities.Story;
import com.vawndev.spring_boot_readnovel.Entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FavoriteStoryRepository extends JpaRepository<FavoriteStory, String> {

    boolean existsByUserAndStory(User user, Story story);

    Optional<FavoriteStory> findByUserAndStory(User user, Story story);

    Page<FavoriteStory> findAllByUserOrderByCreatedAtDesc(User user, Pageable pageable);
}

