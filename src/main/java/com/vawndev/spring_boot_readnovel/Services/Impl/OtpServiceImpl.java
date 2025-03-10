package com.vawndev.spring_boot_readnovel.Services.Impl;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.vawndev.spring_boot_readnovel.Services.OtpService;

@Service
public class OtpServiceImpl implements OtpService {

    private final Map<String, String> otpStorage = new HashMap<>();
    private final SecureRandom random = new SecureRandom();

    @Override
    public String generateOtp(String email) {
        String otp = String.format("%06d", random.nextInt(1000000));
        otpStorage.put(email, otp);
        return otp;
    }

    @Override
    public boolean validateOtp(String email, String otp) {
        return otp.equals(otpStorage.get(email));
    }
}
