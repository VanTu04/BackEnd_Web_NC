package com.vawndev.spring_boot_readnovel.Utils.Help;

import com.vawndev.spring_boot_readnovel.Entities.User;
import lombok.experimental.UtilityClass;

import java.math.BigDecimal;

@UtilityClass
public class UserHelper {
    public BigDecimal getPriceByUser(BigDecimal price, User user) {
        if (user != null && user.getSubscription() != null) {
            return BigDecimal.ZERO;
        }
        return price != null ? price : BigDecimal.ZERO;
    }

}
