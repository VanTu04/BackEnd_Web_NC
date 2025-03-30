package com.vawndev.spring_boot_readnovel.Services;

import com.vawndev.spring_boot_readnovel.Dto.Responses.Payment.PaymentResponse;
import com.vawndev.spring_boot_readnovel.Dto.Responses.Payment.WalletTransactionResponse;

import jakarta.servlet.http.HttpServletRequest;

public interface PaymentService {
    PaymentResponse createVNPayPayment(HttpServletRequest request, int amount);

    PaymentResponse createVNPayPayment(HttpServletRequest request);

    WalletTransactionResponse createWalletTransaction(String vnp_TxnRef);

    void purchaseChapter(String chapterId);

    void purchaseStory(String storyId);
}
