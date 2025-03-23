package com.vawndev.spring_boot_readnovel.Services.Impl;

import com.vawndev.spring_boot_readnovel.Configurations.VNPayConfig;
import com.vawndev.spring_boot_readnovel.Dto.Responses.Payment.PaymentResponse;
import com.vawndev.spring_boot_readnovel.Dto.Responses.Payment.WalletTransactionResponse;
import com.vawndev.spring_boot_readnovel.Entities.*;
import com.vawndev.spring_boot_readnovel.Enum.TransactionStatus;
import com.vawndev.spring_boot_readnovel.Enum.TransactionType;
import com.vawndev.spring_boot_readnovel.Exceptions.AppException;
import com.vawndev.spring_boot_readnovel.Exceptions.ErrorCode;
import com.vawndev.spring_boot_readnovel.Repositories.*;
import com.vawndev.spring_boot_readnovel.Services.PaymentService;
import com.vawndev.spring_boot_readnovel.Utils.VNPayUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final VNPayConfig payConfig;
    private final UserRepository userRepository;
    private final WalletTransactionRepository walletTransactionRepository;
    private final OwnershipRepository ownershipRepository;
    private final StoryRepository storyRepository;
    private final ChapterRepository chapterRepository;

    @Override
    public PaymentResponse createVNPayPayment(HttpServletRequest request, int a) {
        long amount = a * 100L;
        String bankCode = request.getParameter("bankCode");
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        var user = userRepository.findByEmail(email).orElseThrow(() -> new AppException(ErrorCode.OBJECT_NOT_EXISTED, "User"));

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

        WalletTransaction walletTransaction = walletTransactionRepository.findById(vnp_TxnRef).orElseThrow(() -> new AppException(ErrorCode.OBJECT_NOT_FOUND));
        walletTransaction.setStatus(TransactionStatus.COMPLETED);

        User user = userRepository.findById(walletTransaction.getUser().getId()).orElseThrow(() -> new AppException(ErrorCode.OBJECT_NOT_EXISTED));
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


    private void processPurchase(String description, List<Chapter> chapters) {
        // kiểm tra xem người dùng đã đăng nhập chưa
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user  = userRepository.findByEmail(email).orElseThrow(() -> new AppException(ErrorCode.UNAUTHORIZED, "User"));

        // Lọc ra các chương user chưa sở hữu
        List<Chapter> chaptersToBuy = chapters.stream()
                .filter(ch -> !ownershipRepository.existsByUserAndChapter(user, ch))
                .toList();

        if (chaptersToBuy.isEmpty()) {
            throw new AppException(ErrorCode.CONFLICT, "You already own all selected chapters");
        }

        // Tính tổng giá trị của các chương chưa sở hữu
        BigDecimal finalCost = chaptersToBuy.stream()
                .map(Chapter::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // kiểm tra số dư
        if (user.getBalance().compareTo(finalCost) < 0) {
            throw new AppException(ErrorCode.NOT_ENOUGH, "Your Wallet");
        }

        // trừ tiền ví của người dùng
        user.setBalance(user.getBalance().subtract(finalCost));
        userRepository.save(user);

        // lưu các chapter người dùng mua vào ownership
        List<Ownership> ownerships = chapters.stream()
                .map(ch -> Ownership.builder().chapter(ch).user(user).build())
                .collect(Collectors.toList());
        ownershipRepository.saveAll(ownerships);

        // lưu hóa đơn
        walletTransactionRepository.save(WalletTransaction.builder()
                .user(user)
                .transactionType(TransactionType.PURCHASE)
                .description(description)
                .amount(finalCost)
                .status(TransactionStatus.COMPLETED)
                .build());
    }

    @Override
    @Transactional
    @PreAuthorize("hasAnyRole('AUTHOR','ADMIN', 'CUSTOMER')")
    public void purchaseChapter(String chapterId) {
        Chapter chapter = chapterRepository.findById(chapterId)
                .orElseThrow(() -> new AppException(ErrorCode.INVALID_CHAPTER));
        processPurchase("Purchased chapter: " + chapter.getTitle(), List.of(chapter));
    }

    @Override
    @Transactional
    @PreAuthorize("hasAnyRole('AUTHOR','ADMIN', 'CUSTOMER')")
    public void purchaseStory(String storyId) {
        Story story = storyRepository.findById(storyId)
                .orElseThrow(() -> new AppException(ErrorCode.INVALID_STORY));
        processPurchase("Purchased story: " + story.getTitle(), story.getChapters());
    }
}
