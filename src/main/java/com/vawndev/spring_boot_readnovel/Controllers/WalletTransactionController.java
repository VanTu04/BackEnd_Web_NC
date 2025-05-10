package com.vawndev.spring_boot_readnovel.Controllers;

import com.vawndev.spring_boot_readnovel.Dto.Responses.ApiResponse;
import com.vawndev.spring_boot_readnovel.Dto.Responses.PageResponse;
import com.vawndev.spring_boot_readnovel.Dto.Responses.Payment.WalletTransactionResponse;
import com.vawndev.spring_boot_readnovel.Services.WalletTransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/wallet")
@RequiredArgsConstructor
public class WalletTransactionController {
    private final WalletTransactionService walletTransactionService;

    @GetMapping("/admin")
    public ApiResponse<?> findAll(@RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {
        return ApiResponse.<PageResponse<WalletTransactionResponse>>builder()
                .message("Successfully")
                .result(walletTransactionService.getAllWalletTransactions(page, size))
                .build();
    }

    @GetMapping("")
    public ApiResponse<?> findAllById(@RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {
        return ApiResponse.<PageResponse<WalletTransactionResponse>>builder()
                .message("Successfully")
                .result(walletTransactionService.getWalletTransactionsByUserId(page, size))
                .build();
    }
}
