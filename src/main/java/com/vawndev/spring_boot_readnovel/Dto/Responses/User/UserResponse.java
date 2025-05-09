package com.vawndev.spring_boot_readnovel.Dto.Responses.User;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserResponse {
    private String id;
    private String fullName;
    private String email;
    private LocalDate dateOfBirth;
    private String imageUrl;
    private List<String> role;
    private BigDecimal balance;
}
