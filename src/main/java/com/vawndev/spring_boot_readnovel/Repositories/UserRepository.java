package com.vawndev.spring_boot_readnovel.Repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.vawndev.spring_boot_readnovel.Entities.User;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByEmail(String email);
    Optional<User> existsUserByEmail(String email);
    Optional<User> findById(String id);
}