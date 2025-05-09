package com.vawndev.spring_boot_readnovel.Dto.Responses.Subscription;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class RolePlanRespone {
    private boolean author_role;
    private BigDecimal price;
}
