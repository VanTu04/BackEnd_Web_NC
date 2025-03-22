package com.vawndev.spring_boot_readnovel.Services;

public interface EmailService {
    void sendOtpEmail(String toEmail, String subject, String body);
}

