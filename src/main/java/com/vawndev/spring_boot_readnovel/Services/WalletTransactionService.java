package com.vawndev.spring_boot_readnovel.Services;

import com.vawndev.spring_boot_readnovel.Dto.Responses.PageResponse;
import com.vawndev.spring_boot_readnovel.Dto.Responses.Payment.WalletTransactionResponse;

public interface WalletTransactionService {
    PageResponse<WalletTransactionResponse> getAllWalletTransactions(int page, int size);
    PageResponse<WalletTransactionResponse> getWalletTransactionsByUserId(int page, int size);
}
