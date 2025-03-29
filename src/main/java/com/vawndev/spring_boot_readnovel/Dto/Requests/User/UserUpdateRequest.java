package com.vawndev.spring_boot_readnovel.Dto.Requests.User;

import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserUpdateRequest {
    private String password;
    private String fullName;
    private LocalDate dateOfBirth;
    private List<String> roles;
}
