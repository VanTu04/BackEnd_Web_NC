package com.vawndev.spring_boot_readnovel.Entities;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "wallets")
public class Wallet extends BaseEntity {
    @OneToOne
    private User user;

    private BigDecimal balance;
}
