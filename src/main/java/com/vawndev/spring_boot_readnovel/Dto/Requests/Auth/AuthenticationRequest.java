package com.vawndev.spring_boot_readnovel.Dto.Requests.Auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthenticationRequest {
    @Email(message = "EMAIL_INVALID")
    @NotBlank(message = "BLANK_EMAIL")
    private String email;

    @Size(min=4, message = "INVALID_PASSWORD")
    private String password;

}
