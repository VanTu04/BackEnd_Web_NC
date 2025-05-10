package com.vawndev.spring_boot_readnovel.Entities;

import java.math.BigDecimal;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PurchaseHistory extends BaseEntity {
    @ManyToOne
    private User user;
    @ManyToOne
    private Chapter chapter;
    private BigDecimal balance;
    private BigDecimal price;

}
