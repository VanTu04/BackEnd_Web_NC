package com.vawndev.spring_boot_readnovel.Services;

import com.vawndev.spring_boot_readnovel.Dto.Requests.PageRequest;
import com.vawndev.spring_boot_readnovel.Dto.Requests.WithdrawRequest;
import com.vawndev.spring_boot_readnovel.Dto.Responses.PageResponse;
import com.vawndev.spring_boot_readnovel.Dto.Responses.WithdrawResponse;
import com.vawndev.spring_boot_readnovel.Enum.TransactionStatus;

public interface WithdrawService {
    WithdrawResponse withdraw(WithdrawRequest withdrawRequest);
    WithdrawResponse editWithdraw(String withdraw_id, TransactionStatus status);
    PageResponse<WithdrawResponse> getWithdraws(TransactionStatus status, PageRequest req);
    WithdrawResponse approvedByAdmin(String withdrawId, TransactionStatus status);
}
