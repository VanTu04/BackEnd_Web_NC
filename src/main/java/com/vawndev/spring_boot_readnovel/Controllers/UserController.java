package com.vawndev.spring_boot_readnovel.Controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vawndev.spring_boot_readnovel.Dto.Requests.User.ConfirmOtpRequest;
import com.vawndev.spring_boot_readnovel.Dto.Requests.User.UserCreationRequest;
import com.vawndev.spring_boot_readnovel.Dto.Responses.ApiResponse;
import com.vawndev.spring_boot_readnovel.Dto.Responses.User.UserResponse;
import com.vawndev.spring_boot_readnovel.Exceptions.AppException;
import com.vawndev.spring_boot_readnovel.Exceptions.ErrorCode;
import com.vawndev.spring_boot_readnovel.Services.OtpService;
import com.vawndev.spring_boot_readnovel.Services.UserService;
import com.vawndev.spring_boot_readnovel.Utils.AesEncryptionUtil;
import com.vawndev.spring_boot_readnovel.Utils.Help.JsonHelper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final OtpService otpService;
    private final ObjectMapper objectMapper;
    private final AesEncryptionUtil aesEncryptionUtil;
//    @PostMapping("")
//    public ApiResponse<UserResponse> createUser(@RequestBody @Valid UserCreationRequest userCreationRequest, @RequestParam String otp) {
//        if (!userCreationRequest.isPasswordMatching()) {
//            throw new AppException(ErrorCode.PASSWORD_MISMATCH);
//        }
//
//        if (otpService.validateOtp(userCreationRequest.getEmail(), otp)) {
//            return ApiResponse.<UserResponse>builder().result(userService.createUser(userCreationRequest)).build();
//        } else {
//            throw new AppException(ErrorCode.INVALID, "OTP is invalid");
//        }
//    }

    @PostMapping("/pre-register")
    public ApiResponse<String> preRegister(@RequestBody @Valid UserCreationRequest request) throws JsonProcessingException {
        return ApiResponse.<String>builder()
                .result(userService.handlePreRegister(request))
                .build();
    }

    @PostMapping("/confirm-register")
    public ApiResponse<UserResponse> confirmRegister(@RequestBody @Valid ConfirmOtpRequest confirmRequest) throws JsonProcessingException {
        return ApiResponse.<UserResponse>builder()
                .result(userService.handleConfirmRegister(confirmRequest))
                .build();
    }


    @PostMapping("/upgrade")
    public ApiResponse<UserResponse> upgradeAccount(@RequestBody @Valid UserCreationRequest userCreationRequest) {
        return ApiResponse.<UserResponse>builder().result(userService.createUser(userCreationRequest)).build();
    }

//    @PostMapping("/request-otp")
//    public ApiResponse<Void> requestOtp(@RequestParam String email) {
//        otpService.sendOtp(email);
//        return ApiResponse.<Void>builder().build();
//    }

    // Forgot Password - Request OTP
    @PostMapping("/forgot-password/request-otp")
    public ApiResponse<Void> forgotPasswordRequestOtp(@RequestParam String email) {
        // Send OTP for password reset
        otpService.sendOtp(email);
        return ApiResponse.<Void>builder().message("OTP sent to registered email").build();
    }

    // Forgot Password - Reset Password
    @PostMapping("/forgot-password/reset")
    public ApiResponse<Void> resetPassword(@RequestParam String email,
                                           @RequestParam String otp,
                                           @RequestParam String newPassword,
                                           @RequestParam String confirmPassword) {
        // Check if passwords match
        if (!newPassword.equals(confirmPassword)) {
            throw new AppException(ErrorCode.PASSWORD_MISMATCH, "Passwords do not match");
        }

        // Validate OTP
        if (!otpService.validateOtp(email, otp)) {
            throw new AppException(ErrorCode.INVALID, "Invalid OTP");
        }

        // Reset password
        userService.resetPassword(email, newPassword);

        return ApiResponse.<Void>builder().message("Password reset successfully").build();
    }
}
