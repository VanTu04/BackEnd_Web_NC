package com.vawndev.spring_boot_readnovel.Services.Impl;

import java.math.BigDecimal;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.vawndev.spring_boot_readnovel.Configurations.VNPayConfig;
import com.vawndev.spring_boot_readnovel.Dto.Responses.Payment.PaymentResponse;
import com.vawndev.spring_boot_readnovel.Dto.Responses.Payment.WalletTransactionResponse;
import com.vawndev.spring_boot_readnovel.Entities.User;
import com.vawndev.spring_boot_readnovel.Entities.WalletTransaction;
import com.vawndev.spring_boot_readnovel.Enum.TransactionStatus;
import com.vawndev.spring_boot_readnovel.Enum.TransactionType;
import com.vawndev.spring_boot_readnovel.Exceptions.AppException;
import com.vawndev.spring_boot_readnovel.Exceptions.ErrorCode;
import com.vawndev.spring_boot_readnovel.Repositories.UserRepository;
import com.vawndev.spring_boot_readnovel.Repositories.WalletTransactionRepository;
import com.vawndev.spring_boot_readnovel.Services.PaymentService;
import com.vawndev.spring_boot_readnovel.Utils.VNPayUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    @Override
    public void purchaseStory(String storyId) {
        // Implement the logic for purchasing a story
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public void purchaseChapter(String chapterId) {
        // Implement the logic for purchasing a chapter
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public PaymentResponse createVNPayPayment(HttpServletRequest request, int amount) {
        // Implement the logic for creating a VNPay payment with an amount
        throw new UnsupportedOperationException("Not implemented yet");
    }

    private final VNPayConfig payConfig;
    private final UserRepository userRepository;
    private final WalletTransactionRepository walletTransactionRepository;

    @Override
    public PaymentResponse createVNPayPayment(HttpServletRequest request) {
        long amount = Integer.parseInt(request.getParameter("amount")) * 100L;
        String bankCode = request.getParameter("bankCode");
        String userId = request.getParameter("userId");
        User user = userRepository.findById(userId).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        WalletTransaction walletTransaction = walletTransactionRepository.save(
                WalletTransaction.builder()
                        .status(TransactionStatus.PENDING)
                        .user(user).description("VNPay Transaction deposit")
                        .transactionType(TransactionType.DEPOSIT)
                        .amount(new BigDecimal(amount / 100L)
                        ).build()
        );

        Map<String, String> vnpParamsMap = payConfig.getVNPayConfig(walletTransaction.getId(), amount/100L);
        vnpParamsMap.put("vnp_Amount", String.valueOf(amount));
        if (bankCode != null && !bankCode.isEmpty()) {
            vnpParamsMap.put("vnp_BankCode", bankCode);
        }
        vnpParamsMap.put("vnp_IpAddr", VNPayUtil.getIpAddress(request));
        //build query url
        String queryUrl = VNPayUtil.getPaymentURL(vnpParamsMap, true);
        String hashData = VNPayUtil.getPaymentURL(vnpParamsMap, false);
        String vnpSecureHash = VNPayUtil.hmacSHA512(payConfig.getSecretKey(), hashData);
        queryUrl += "&vnp_SecureHash=" + vnpSecureHash;
        String paymentUrl = payConfig.getVnp_PayUrl() + "?" + queryUrl;

                return PaymentResponse.builder()
                        .code("ok")
                        .message("success")
                        .paymentUrl(paymentUrl)
                        .build();
            }



    @Override
    public WalletTransactionResponse createWalletTransaction(String vnp_TxnRef) {

        WalletTransaction walletTransaction = walletTransactionRepository.findById(vnp_TxnRef).orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND));
        walletTransaction.setStatus(TransactionStatus.COMPLETED);

        User user = userRepository.findById(walletTransaction.getUser().getId()).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        user.setBalance(user.getBalance().add(walletTransaction.getAmount()));
        userRepository.save(user);

        walletTransactionRepository.save(walletTransaction);

        return WalletTransactionResponse.builder()
                .email(user.getEmail())
                .fullName(user.getFullName())
                .balance(user.getBalance())
                .amount(walletTransaction.getAmount())
                .description(walletTransaction.getDescription())
                .status(walletTransaction.getStatus())
                .build();
    }
}