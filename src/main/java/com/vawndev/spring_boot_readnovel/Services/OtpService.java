package com.vawndev.spring_boot_readnovel.Services;

public interface OtpService {
    String generateOtp(String email);
    boolean validateOtp(String email, String otp);
    void sendOtp(String email);
    String refreshOtp(String email);
}
