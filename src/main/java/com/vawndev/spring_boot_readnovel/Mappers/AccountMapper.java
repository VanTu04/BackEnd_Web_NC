package com.vawndev.spring_boot_readnovel.Mappers;

import com.vawndev.spring_boot_readnovel.Dto.Requests.AccountUpdateRequest;
import com.vawndev.spring_boot_readnovel.Dto.Responses.AccountResponse;
import com.vawndev.spring_boot_readnovel.Models.Account;
import org.springframework.stereotype.Component;

@Component
public class AccountMapper {

    public AccountResponse toResponse(Account account) {
        return AccountResponse.builder()
                .id(account.getId())
                .fullName(account.getFullName())
                .email(account.getEmail())
                .build();
    }

    public void updateAccountFromRequest(AccountUpdateRequest request, Account account) {
        account.setFullName(request.getFullName());
        account.setEmail(request.getEmail());
        account.setPassword(request.getPassword());
    }
}