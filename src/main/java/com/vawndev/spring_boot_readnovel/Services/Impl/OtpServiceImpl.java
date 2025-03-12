package com.vawndev.spring_boot_readnovel.Services.Impl;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vawndev.spring_boot_readnovel.Entities.Otp;
import com.vawndev.spring_boot_readnovel.Repositories.OtpRepository;
import com.vawndev.spring_boot_readnovel.Services.OtpService;

@Service
public class OtpServiceImpl implements OtpService {

    @Autowired
    private OtpRepository otpRepository;

    private final SecureRandom random = new SecureRandom();

    @Override
    public String generateOtp(String email) {
        String otp = String.format("%06d", random.nextInt(1000000));
        Otp otpEntity = Otp.builder()
                .email(email)
                .otp(otp)
                .expirationTime(LocalDateTime.now().plusMinutes(3)) // Hết hạn sau 3 phút
                .build();
        otpRepository.save(otpEntity);
        return otp;
    }

    @Override
    public boolean validateOtp(String email, String otp) {
        Optional<Otp> otpEntity = otpRepository.findByEmail(email);
        if (otpEntity.isPresent()) {
            Otp storedOtp = otpEntity.get();
            if (storedOtp.getExpirationTime().isAfter(LocalDateTime.now())) {
                return storedOtp.getOtp().equals(otp);
            } else {
                otpRepository.deleteByEmail(email); // Xóa OTP đã hết hạn
            }
        }
        return false;
    }
}
