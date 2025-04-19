package com.vawndev.spring_boot_readnovel.Dto.Responses;

import com.vawndev.spring_boot_readnovel.Enum.TransactionStatus;
import com.vawndev.spring_boot_readnovel.Enum.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WithdrawResponse  {
    private String content;
    private BigDecimal AmountWithdrawn;
    private BigDecimal RemainingAmount;
    private String CommissionAmount;
    private String Bankname;
    private TransactionStatus status;
    private TransactionType type;
    private String createdAt;
}
