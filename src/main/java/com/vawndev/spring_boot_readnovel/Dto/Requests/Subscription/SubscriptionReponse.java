package com.vawndev.spring_boot_readnovel.Dto.Requests.Subscription;

import lombok.Data;

@Data
public class SubscriptionReponse {
    private String accountNumber;
    private String accountName;
    private String bankName;
}
