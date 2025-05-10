package com.vawndev.spring_boot_readnovel.Dto.Responses.Payment;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.vawndev.spring_boot_readnovel.Entities.User;
import com.vawndev.spring_boot_readnovel.Enum.TransactionStatus;
import com.vawndev.spring_boot_readnovel.Enum.TransactionType;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WalletTransactionResponse {
    private String id;
    
    private String fullName;

    private String email;

    private TransactionType transactionType;

    private String description;

    private BigDecimal amount;

    private BigDecimal balance;

    private TransactionStatus status;
}
