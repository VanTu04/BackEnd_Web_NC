package com.vawndev.spring_boot_readnovel.Services;

import com.vawndev.spring_boot_readnovel.Dto.Responses.Payment.PaymentResponse;
import com.vawndev.spring_boot_readnovel.Dto.Responses.Payment.WalletTransactionResponse;
import com.vawndev.spring_boot_readnovel.Entities.WalletTransaction;
import jakarta.servlet.http.HttpServletRequest;

public interface PaymentService {
    PaymentResponse createVNPayPayment(HttpServletRequest request);

    WalletTransactionResponse createWalletTransaction(long amount);
}
