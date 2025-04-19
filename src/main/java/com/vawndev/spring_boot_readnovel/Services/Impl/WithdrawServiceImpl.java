package com.vawndev.spring_boot_readnovel.Services.Impl;

import com.vawndev.spring_boot_readnovel.Dto.Requests.PageRequest;
import com.vawndev.spring_boot_readnovel.Dto.Requests.WithdrawRequest;
import com.vawndev.spring_boot_readnovel.Dto.Responses.PageResponse;
import com.vawndev.spring_boot_readnovel.Dto.Responses.WithdrawResponse;
import com.vawndev.spring_boot_readnovel.Entities.User;
import com.vawndev.spring_boot_readnovel.Entities.WithdrawTransaction;
import com.vawndev.spring_boot_readnovel.Enum.TransactionStatus;
import com.vawndev.spring_boot_readnovel.Enum.TransactionType;
import com.vawndev.spring_boot_readnovel.Exceptions.AppException;
import com.vawndev.spring_boot_readnovel.Exceptions.ErrorCode;
import com.vawndev.spring_boot_readnovel.Repositories.UserRepository;
import com.vawndev.spring_boot_readnovel.Repositories.WithdrawTransactionRepository;
import com.vawndev.spring_boot_readnovel.Services.WithdrawService;
import com.vawndev.spring_boot_readnovel.Utils.Help.TokenHelper;
import com.vawndev.spring_boot_readnovel.Utils.JwtUtils;
import com.vawndev.spring_boot_readnovel.Utils.PaginationUtil;
import com.vawndev.spring_boot_readnovel.Utils.TimeZoneConvert;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WithdrawServiceImpl implements WithdrawService {
    private final JwtUtils jwtUtils;
    private final TokenHelper tokenHelper;
    private final WithdrawTransactionRepository withdrawTransactionRepository;
    private final UserRepository userRepository;
    private User getAuthenticatedUser() {
        return tokenHelper.getUserO2Auth();
    }

    @Override
    public WithdrawResponse withdraw( WithdrawRequest withdrawRequest) {

        User user = getAuthenticatedUser();;
        BigDecimal balance = Optional.ofNullable(user.getBalance()).orElse(BigDecimal.ZERO);

        if (balance.compareTo(withdrawRequest.getAmountRequest()) < 0) {
            throw new AppException(ErrorCode.FAILED_PAYMENT, "You don't have enough money to withdraw");
        }

        BigDecimal newBalance = balance.subtract(withdrawRequest.getAmountRequest());
        BigDecimal conversionMoney = withdrawRequest.getAmountRequest().multiply(BigDecimal.valueOf(1000));

        // Tạo giao dịch rút tiền
        WithdrawTransaction withdrawTransaction = WithdrawTransaction
                .builder()
                .user(user)
                .accountName(withdrawRequest.getAccountName().toUpperCase())
                .accountNumber(withdrawRequest.getAccountNumber())
                .amountRequest(withdrawRequest.getAmountRequest())
                .amount(newBalance)
                .conversionMoney(conversionMoney)
                .bankName(withdrawRequest.getBankName())
                .status(TransactionStatus.PENDING)
                .transactionType(TransactionType.WITHDRAW)
                .description("")
                .build();

        withdrawTransaction.setDescription(withdrawTransaction.generateContent(TransactionStatus.PENDING, null));

        user.setBalance(newBalance);

        withdrawTransactionRepository.save(withdrawTransaction);
        userRepository.save(user);

        // Trả về phản hồi
        return WithdrawResponse
                .builder()
                .type(withdrawTransaction.getTransactionType())
                .AmountWithdrawn(withdrawTransaction.getAmountRequest())
                .content(withdrawTransaction.getDescription())
                .status(withdrawTransaction.getStatus())
                .createdAt(TimeZoneConvert.convertUtcToUserTimezone(withdrawTransaction.getCreatedAt()))
                .Bankname(withdrawTransaction.getBankName())
                .RemainingAmount(newBalance)
                .build();
    }


    @Override
    @Transactional
    public WithdrawResponse editWithdraw( String withdrawId, TransactionStatus status) {
        User user = getAuthenticatedUser();;

        WithdrawTransaction withdrawTransaction = withdrawTransactionRepository
                .findByIdAndUser(withdrawId, user)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND, "Withdraw"));

        if (withdrawTransaction.getStatus() == TransactionStatus.COMPLETED) {
            throw new AppException(ErrorCode.FAILED_PAYMENT, "Your withdraw has already been completed before");
        }

        BigDecimal userBalance = Optional.ofNullable(user.getBalance()).orElse(BigDecimal.ZERO);
        BigDecimal withdrawAmount = withdrawTransaction.getAmountRequest();

        if (status == TransactionStatus.PENDING && userBalance.compareTo(withdrawAmount) < 0) {
            throw new AppException(ErrorCode.FAILED_PAYMENT, "Not enough balance");
        }

        // ✅ Xử lý số dư
        if (withdrawTransaction.getStatus() == TransactionStatus.PENDING) {
            userBalance = userBalance.add(withdrawAmount); // Hoàn tiền nếu trước đó đã trừ khi PENDING
        }

        if (status == TransactionStatus.PENDING) {
            userBalance = userBalance.subtract(withdrawAmount);
        }

        user.setBalance(userBalance);
        withdrawTransaction.setStatus(status);
        withdrawTransaction.setDeleteAt(status == TransactionStatus.FAILED ? Instant.now() : null);

        withdrawTransactionRepository.save(withdrawTransaction);
        userRepository.save(user);

        return WithdrawResponse.builder()
                .type(withdrawTransaction.getTransactionType())
                .AmountWithdrawn(withdrawAmount)
                .content(withdrawTransaction.getDescription())
                .status(withdrawTransaction.getStatus())
                .createdAt(TimeZoneConvert.convertUtcToUserTimezone(withdrawTransaction.getCreatedAt()))
                .RemainingAmount(userBalance)
                .Bankname(withdrawTransaction.getBankName())
                .build();
    }


    @Override
    @Transactional
//    @PreAuthorize("hasRole('ADMIN')")
    public WithdrawResponse approvedByAdmin( String withdrawId, TransactionStatus status) {
        getAuthenticatedUser();;

        WithdrawTransaction withdrawTransaction = withdrawTransactionRepository
                .findById(withdrawId)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND, "Withdraw"));

        if (withdrawTransaction.getStatus() == TransactionStatus.COMPLETED) {
            throw new AppException(ErrorCode.FAILED_PAYMENT, "This withdraw has already been completed before");
        }

        User user = withdrawTransaction.getUser();

        if (status == TransactionStatus.FAILED) {
            user.setBalance(user.getBalance().add(withdrawTransaction.getAmountRequest()));
        }

        withdrawTransaction.setStatus(status);
        withdrawTransaction.setDeleteAt(status == TransactionStatus.FAILED ? Instant.now() : null);

        withdrawTransactionRepository.save(withdrawTransaction);
        userRepository.save(user);

        return WithdrawResponse.builder()
                .type(withdrawTransaction.getTransactionType())
                .AmountWithdrawn(withdrawTransaction.getAmountRequest())
                .content(withdrawTransaction.getDescription())
                .status(status)
                .createdAt(TimeZoneConvert.convertUtcToUserTimezone(withdrawTransaction.getCreatedAt()))
                .build();
    }

    @Override
    public PageResponse<WithdrawResponse> getWithdraws( TransactionStatus status,PageRequest req) {
        User user = getAuthenticatedUser();;
        Pageable pageable= PaginationUtil.createPageable(req.getPage(), req.getLimit());
        Page<WithdrawTransaction> withdrawTransaction = withdrawTransactionRepository.findAllByUserAndStatus(user, status ,pageable);
        List <WithdrawResponse> withdrawResponses = withdrawTransaction.getContent().stream().map(withdraw->WithdrawResponse
                .builder()
                .status(withdraw.getStatus())
                .createdAt(TimeZoneConvert.convertUtcToUserTimezone(withdraw.getCreatedAt()))
                .type(withdraw.getTransactionType())
                .AmountWithdrawn(withdraw.getAmountRequest())
                .Bankname(withdraw.getBankName())
                .type(withdraw.getTransactionType())
                .content(withdraw.getDescription())
                .RemainingAmount(withdraw.getAmount())
                .CommissionAmount(withdraw.getConversionMoney() + " VND")
                .build()
        ).collect(Collectors.toList());
        return  PageResponse.<WithdrawResponse>builder()
                                .data(withdrawResponses)
                                .total(withdrawTransaction.getTotalPages())
                                .limit(withdrawTransaction.getSize())
                                .page(withdrawTransaction.getNumber())
                                .build();


    }

    @Scheduled(fixedDelay = 86400000)
    public void cleanOldWithdrawTransactions() {
        Instant expiredTime = Instant.now().minus(3, ChronoUnit.DAYS);
        withdrawTransactionRepository.deleteOldWithdrawTransactions(expiredTime);
        System.out.println("Has remove old withdraw transactions");
    }

}
