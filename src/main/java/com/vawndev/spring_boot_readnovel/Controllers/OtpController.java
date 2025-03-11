package com.vawndev.spring_boot_readnovel.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
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

    @GetMapping("/test")
    public String testEndpoint() {
        return "Test endpoint is working!";
    }

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
}
