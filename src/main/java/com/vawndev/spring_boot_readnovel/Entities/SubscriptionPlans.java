package com.vawndev.spring_boot_readnovel.Entities;

import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SubscriptionPlans extends BaseEntity{
    private String type;
    private Long expired;
    private BigDecimal price;
}
