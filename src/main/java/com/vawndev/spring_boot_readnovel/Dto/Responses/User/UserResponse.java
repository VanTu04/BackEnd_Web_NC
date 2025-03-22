package com.vawndev.spring_boot_readnovel.Dto.Responses.User;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserResponse {
    private String fullName;
    private String email;
}
