package com.vawndev.spring_boot_readnovel.Services.Impl;

import com.vawndev.spring_boot_readnovel.Dto.Responses.PageResponse;
import com.vawndev.spring_boot_readnovel.Dto.Responses.Payment.WalletTransactionResponse;
import com.vawndev.spring_boot_readnovel.Entities.WalletTransaction;
import com.vawndev.spring_boot_readnovel.Exceptions.AppException;
import com.vawndev.spring_boot_readnovel.Exceptions.ErrorCode;
import com.vawndev.spring_boot_readnovel.Repositories.UserRepository;
import com.vawndev.spring_boot_readnovel.Repositories.WalletTransactionRepository;
import com.vawndev.spring_boot_readnovel.Services.WalletTransactionService;
import com.vawndev.spring_boot_readnovel.Utils.PaginationUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WalletTransactionServiceImpl implements WalletTransactionService {

    private final UserRepository userRepository;
    private final WalletTransactionRepository walletTransactionRepository;

    @Override
    @PreAuthorize("hasAuthority('ADMIN')")
    public PageResponse<WalletTransactionResponse> getAllWalletTransactions(int page, int size) {

        Pageable pageable = PaginationUtil.createPageable(page, size, Sort.Direction.DESC, "createdAt");
        Page<WalletTransaction> walletTransactions = walletTransactionRepository.findAll(pageable);
        return PageResponse.<WalletTransactionResponse>builder()
                .data(walletTransactions.map(this::toResponse).getContent())
                .page(walletTransactions.getNumber())
                .limit(walletTransactions.getSize())
                .total((int) walletTransactions.getTotalElements())
                .build();
    }

    private WalletTransactionResponse toResponse(WalletTransaction e) {
        return WalletTransactionResponse.builder()
                .id(e.getId())
                .email(e.getUser().getEmail())
                .fullName(e.getUser().getFullName())
                .balance(e.getUser().getBalance())
                .description(e.getDescription())
                .amount(e.getAmount())
                .status(e.getStatus())
                .transactionType(e.getTransactionType())
                .build();
    }

    @Override
    public PageResponse<WalletTransactionResponse> getWalletTransactionsByUserId(int page, int size) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        var user = userRepository.findByEmail(email).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        Pageable pageable = PaginationUtil.createPageable(page, size, Sort.Direction.DESC, "createdAt");
        Page<WalletTransaction> walletTransactions = walletTransactionRepository.findByUser(user, pageable);
        return PageResponse.<WalletTransactionResponse>builder()
                .data(walletTransactions.map(this::toResponse).getContent())
                .page(walletTransactions.getNumber())
                .limit(walletTransactions.getSize())
                .total((int) walletTransactions.getTotalElements())
                .build();
    }
}
