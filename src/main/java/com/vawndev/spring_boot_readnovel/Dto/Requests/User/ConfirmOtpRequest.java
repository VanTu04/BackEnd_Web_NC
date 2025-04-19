package com.vawndev.spring_boot_readnovel.Dto.Requests.User;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ConfirmOtpRequest {
    @NotBlank(message = "BLANK_DATA")
    private String encryptedData;
    @NotBlank(message = "BLANK_DATA")
    private String otp;
}
