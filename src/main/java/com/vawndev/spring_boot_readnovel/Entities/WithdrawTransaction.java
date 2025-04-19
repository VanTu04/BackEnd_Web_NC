package com.vawndev.spring_boot_readnovel.Entities;

import com.vawndev.spring_boot_readnovel.Enum.TransactionStatus;
import com.vawndev.spring_boot_readnovel.Enum.TransactionType;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class WithdrawTransaction extends BaseEntity {
    @ManyToOne
    private User user;

    private String description;
    private BigDecimal amount;

    @Column(name = "transaction_type")
    @Enumerated(EnumType.STRING)
    private TransactionType transactionType;

    @Column(name = "transaction_status")
    @Enumerated(EnumType.STRING)
    private TransactionStatus status;

    private BigDecimal conversionMoney;
    private String bankName;
    private String accountNumber;
    private String accountName;
    private BigDecimal amountRequest;

    public String generateContent(TransactionStatus status, String moreText) {
        Map<TransactionStatus, String> contentMap = Map.of(
                TransactionStatus.PENDING, String.format(
                        "Request withdraw from %s with amount %.2f coin, conversion to %.2f VND, please wait until admin approved.",
                        user.getFullName(),
                        Optional.ofNullable(amountRequest),
                        Optional.ofNullable(conversionMoney)
                ),
                TransactionStatus.COMPLETED, "Withdrawal request has been completed successfully.",
                TransactionStatus.FAILED, "Withdrawal request failed due to insufficient funds or other issues."
        );

        String content = contentMap.getOrDefault(status, "Unknown transaction status.");

        if (moreText != null && !moreText.isEmpty()) {
            content += " " + moreText;
        }

        return content;
    }

   }
