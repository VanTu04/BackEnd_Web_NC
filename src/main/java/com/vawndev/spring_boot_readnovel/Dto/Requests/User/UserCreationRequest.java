package com.vawndev.spring_boot_readnovel.Dto.Requests.User;

import jakarta.persistence.Lob;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserCreationRequest {
    @NotBlank(message = "BLANK_NAME")
    private String fullName;

    @Email(message = "INVALID_EMAIL")
    @NotBlank(message = "BLANK_EMAIL")
    private String email;

    @Size(min = 6, message = "INVALID_PASSWORD")
    private String password;

    @Size(min = 6, message = "INVALID_PASSWORD")
    private String retypePassword;

    private LocalDate dateOfBirth;

    private Boolean isActive = true;


    public boolean isPasswordMatching() {
        return password != null && password.equals(retypePassword);
    }
}
