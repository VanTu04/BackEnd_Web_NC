package com.vawndev.spring_boot_readnovel.Dto.Requests.User;

import jakarta.persistence.Lob;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserCreationRequest {
    @NotBlank(message = "Full name is required")
    private String fullName;

    @Email(message = "INVALID_EMAIL")
    @NotBlank(message = "Email is required")
    private String email;

    @Size(min = 6, message = "INVALID_PASSWORD")
    @NotBlank(message = "Password is required")
    private String password;

    @NotBlank(message = "Retype password is required")
    private String retypePassword;

    private LocalDate dateOfBirth;

    private Boolean isActive = true;


    public boolean isPasswordMatching() {
        return password != null && password.equals(retypePassword);
    }
}
