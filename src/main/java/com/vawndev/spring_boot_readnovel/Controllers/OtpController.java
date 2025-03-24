package com.vawndev.spring_boot_readnovel.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.vawndev.spring_boot_readnovel.Services.EmailService;
import com.vawndev.spring_boot_readnovel.Services.OtpService;

@RestController
@RequestMapping("/otp")
public class OtpController {

    @Autowired
    private EmailService emailService;

    @Autowired
    private OtpService otpService;

    @PostMapping("/send")
    public String sendOtp(@RequestParam String email) {
        String otp = otpService.generateOtp(email);
        emailService.sendOtpEmail(email, "Your OTP Code", "Your OTP is: " + otp);
        return "OTP sent to " + email;
    }

    @PostMapping("/validate")
    public String validateOtp(@RequestParam String email, @RequestParam String otp) {
        if (otpService.validateOtp(email, otp)) {
            return "OTP is valid!";
        } else {
            return "Invalid OTP!";
        }
    }

    @PostMapping("/refresh")
    public String refreshOtp(@RequestParam String email) {
        try {
            String newOtp = otpService.refreshOtp(email);
            emailService.sendOtpEmail(email, "Your OTP Code", "Your OTP is: " + newOtp);
            return "New OTP sent to " + email;
        } catch (IllegalStateException e) {
            return e.getMessage();
        } catch (IllegalArgumentException e) {
            return e.getMessage();
        }
    }
}
