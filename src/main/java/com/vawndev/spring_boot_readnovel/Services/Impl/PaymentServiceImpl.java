package com.vawndev.spring_boot_readnovel.Services.Impl;

import com.vawndev.spring_boot_readnovel.Configurations.VNPayConfig;
import com.vawndev.spring_boot_readnovel.Dto.Responses.Payment.PaymentResponse;
import com.vawndev.spring_boot_readnovel.Dto.Responses.Payment.WalletTransactionResponse;
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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;

@Service
@Transactional
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final VNPayConfig payConfig;
    private final UserRepository userRepository;
    private final WalletTransactionRepository walletTransactionRepository;

    @Override
    public PaymentResponse createVNPayPayment(HttpServletRequest request) {
        long amount = Integer.parseInt(request.getParameter("amount")) * 100L;
        String bankCode = request.getParameter("bankCode");

        Map<String, String> vnpParamsMap = payConfig.getVNPayConfig(amount/100L);
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
    public WalletTransactionResponse createWalletTransaction(long amount) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        var user = userRepository.findByEmail(email).orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND, "User"));

        user.setBalance(user.getBalance().add(BigDecimal.valueOf(amount)));
        userRepository.save(user);

        WalletTransaction walletTransaction = walletTransactionRepository.save(
                WalletTransaction.builder()
                        .status(TransactionStatus.COMPLETED)
                        .user(user).description("VNPay Transaction deposit")
                        .transactionType(TransactionType.DEPOSIT)
                        .amount(new BigDecimal(amount)
                        ).build()
        );

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
