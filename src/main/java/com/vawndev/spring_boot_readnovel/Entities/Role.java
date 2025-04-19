package com.vawndev.spring_boot_readnovel.Entities;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.util.Collection;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "role")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String description;

    private BigDecimal price;

    public void setPrice(BigDecimal price) {
        if ("ADMIN".equalsIgnoreCase(this.name)) {
            this.price = new BigDecimal("99999999999999999999999999999999999999.99");
            throw new SecurityException("Cannot change the price of ADMIN role");
        }
        this.price = price;
    }
}
