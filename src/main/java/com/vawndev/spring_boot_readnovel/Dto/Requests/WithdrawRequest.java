package com.vawndev.spring_boot_readnovel.Dto.Requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class WithdrawRequest {

    @NotBlank(message = "Bank name cannot be empty")
    private String bankName;

    @NotBlank(message = "Account number cannot be empty")
    private String accountNumber;

    @NotBlank(message = "Account name cannot be empty")
    private String accountName;

    @NotNull(message = "Amount request cannot be null")
    private BigDecimal amountRequest;
}
