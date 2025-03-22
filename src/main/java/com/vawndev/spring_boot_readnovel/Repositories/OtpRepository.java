package com.vawndev.spring_boot_readnovel.Repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.vawndev.spring_boot_readnovel.Entities.Otp;

@Repository
public interface OtpRepository extends JpaRepository<Otp, String> {
    void deleteByEmail(String email);
    Optional<Otp> findByEmail(String email);
}