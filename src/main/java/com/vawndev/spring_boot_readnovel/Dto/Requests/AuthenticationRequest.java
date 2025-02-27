package com.vawndev.spring_boot_readnovel.Dto.Requests;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthenticationRequest {
    @NotBlank(message = "BLANK_EMAIL")
    private String email;

    @NotBlank(message = "BLANK_PASSWORD")
    private String password;
}
