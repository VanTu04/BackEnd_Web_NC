package com.vawndev.spring_boot_readnovel.Entities;

import com.vawndev.spring_boot_readnovel.Enum.TransactionStatus;
import com.vawndev.spring_boot_readnovel.Enum.TransactionType;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "wallet_transactions")
public class WalletTransaction extends BaseEntity{
    @ManyToOne
    private User user;

    @Column(name = "transaction_type")
    @Enumerated(EnumType.STRING)
    private TransactionType transactionType;

    private String description;

    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    private TransactionStatus status;
}
