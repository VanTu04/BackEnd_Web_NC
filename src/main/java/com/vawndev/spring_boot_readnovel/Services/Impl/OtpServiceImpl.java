package com.vawndev.spring_boot_readnovel.Services.Impl;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vawndev.spring_boot_readnovel.Entities.Otp;
import com.vawndev.spring_boot_readnovel.Repositories.OtpRepository;
import com.vawndev.spring_boot_readnovel.Services.EmailService;
import com.vawndev.spring_boot_readnovel.Services.OtpService;

@Service
public class OtpServiceImpl implements OtpService {

    @Autowired
    private OtpRepository otpRepository;

    @Autowired
    private EmailService emailService;

    private final SecureRandom random = new SecureRandom();

    @Override
    @Transactional
    public String generateOtp(String email) {
        // Xóa tất cả các bản ghi OTP cũ cho email này
        otpRepository.deleteByEmail(email);

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
    @Transactional
    public void sendOtp(String email) {
        // Validate email format
        if (!isValidEmail(email)) {
            throw new IllegalArgumentException("Invalid email format");
        }

        String otp = generateOtp(email);
        emailService.sendOtpEmail(email, "Your OTP Code", "Your OTP is: " + otp);
    }

    // Helper method to validate email
    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$";
        Pattern pattern = Pattern.compile(emailRegex);
        return pattern.matcher(email).matches();
    }

    @Override
    @Transactional
    public boolean validateOtp(String email, String otp) {
        Optional<Otp> otpEntity = otpRepository.findByEmail(email);
        if (otpEntity.isPresent()) {
            Otp storedOtp = otpEntity.get();
            if (storedOtp.getExpirationTime().isAfter(LocalDateTime.now())) {
                if (storedOtp.getOtp().equals(otp)) {
                    otpRepository.deleteByEmail(email); // Xóa OTP đã được sử dụng
                    return true;
                }
            } else {
                otpRepository.deleteByEmail(email); // Xóa OTP đã hết hạn
            }
        }
        return false;
    }

    @Override
    @Transactional
    public String refreshOtp(String email) {
        Optional<Otp> otpEntity = otpRepository.findByEmail(email);
        if (otpEntity.isPresent()) {
            Otp storedOtp = otpEntity.get();
            if (storedOtp.getExpirationTime().isBefore(LocalDateTime.now().minusMinutes(3))) {
                String newOtp = String.format("%06d", random.nextInt(1000000));
                storedOtp.setOtp(newOtp);
                storedOtp.setExpirationTime(LocalDateTime.now().plusMinutes(3)); // Hết hạn sau 3 phút
                otpRepository.save(storedOtp);
                return newOtp;
            } else {
                throw new IllegalStateException("OTP can only be refreshed 3 minutes after the previous OTP was created");
            }
        } else {
            throw new IllegalArgumentException("No OTP found for the provided email");
        }
    }
}
