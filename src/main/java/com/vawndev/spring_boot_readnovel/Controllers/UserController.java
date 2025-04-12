package com.vawndev.spring_boot_readnovel.Controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.vawndev.spring_boot_readnovel.Dto.Requests.User.UserCreationRequest;
import com.vawndev.spring_boot_readnovel.Dto.Requests.User.UserUpdateRequest;
import com.vawndev.spring_boot_readnovel.Dto.Responses.ApiResponse;
import com.vawndev.spring_boot_readnovel.Dto.Responses.User.UserResponse;
import com.vawndev.spring_boot_readnovel.Exceptions.AppException;
import com.vawndev.spring_boot_readnovel.Exceptions.ErrorCode;
import com.vawndev.spring_boot_readnovel.Services.OtpService;
import com.vawndev.spring_boot_readnovel.Services.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final OtpService otpService;

    @PostMapping("/request-otp")
    public ApiResponse<Void> requestOtp(@RequestParam String email) {
        otpService.sendOtp(email);
        return ApiResponse.<Void>builder().build();
    }

    @PostMapping("/create")
    public ApiResponse<UserResponse> createUser(@RequestBody @Valid UserCreationRequest userCreationRequest, @RequestParam String otp) {
        if (!userCreationRequest.isPasswordMatching()) {
            throw new AppException(ErrorCode.PASSWORD_MISMATCH);
        }

        if (otpService.validateOtp(userCreationRequest.getEmail(), otp)) {
            return ApiResponse.<UserResponse>builder().result(userService.createUser(userCreationRequest)).build();
        } else {
            throw new AppException(ErrorCode.INVALID, "OTP is invalid");
        }
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(@PathVariable String id) {
        UserResponse userResponse = userService.getUser(id);
        return ResponseEntity.ok(ApiResponse.<UserResponse>builder().result(userResponse).build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> updateUser(@PathVariable String id, @RequestBody UserUpdateRequest request) {
        userService.updateUser(id, request);
        return ResponseEntity.ok(ApiResponse.<Void>builder().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable String id) {
        userService.deleteUser(id);
        return ResponseEntity.ok(ApiResponse.<Void>builder().build());
    }

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
