package com.vawndev.spring_boot_readnovel.Services;

import com.vawndev.spring_boot_readnovel.Dto.Requests.PageRequest;
import com.vawndev.spring_boot_readnovel.Dto.Requests.WithdrawRequest;
import com.vawndev.spring_boot_readnovel.Dto.Responses.PageResponse;
import com.vawndev.spring_boot_readnovel.Dto.Responses.WithdrawResponse;
import com.vawndev.spring_boot_readnovel.Enum.TransactionStatus;

public interface WithdrawService {
    WithdrawResponse withdraw(String bearerToken, WithdrawRequest withdrawRequest);
    WithdrawResponse editWithdraw(String bearerToken, String withdraw_id, TransactionStatus status);
    PageResponse<WithdrawResponse> getWithdraws(String bearerToken, TransactionStatus status, PageRequest req);
    WithdrawResponse approvedByAdmin(String bearerToken, String withdrawId, TransactionStatus status);
}
