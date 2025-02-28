package com.vawndev.spring_boot_readnovel.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.vawndev.spring_boot_readnovel.Entities.Chapter;

@Repository
public interface ChapterRepository extends JpaRepository<Chapter, String> {
}