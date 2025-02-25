package com.vawndev.spring_boot_readnovel.Dto.Responses;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserResponse {
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
    private String id;
    private String fullName;
    private String email;
    private LocalDate dateOfBirth;
    Set<RoleResponse> roles;
}
