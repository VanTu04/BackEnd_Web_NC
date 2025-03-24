package com.vawndev.spring_boot_readnovel.Dto.Requests.User;

import jakarta.persistence.Lob;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserCreationRequest {
    @Email(message = "INVALID_EMAIL")
    private String email;

    @Size(min = 6, message = "INVALID_PASSWORD")
    private String password;

    private String retypePassword;

    private String firstName;

    private String lastName;

    private LocalDate dateOfBirth;

    @Lob
    private String refreshToken;
}
