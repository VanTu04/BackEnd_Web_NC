package com.vawndev.spring_boot_readnovel.Controllers;

import com.vawndev.spring_boot_readnovel.Dto.Requests.PageRequest;
import com.vawndev.spring_boot_readnovel.Dto.Requests.WithdrawRequest;
import com.vawndev.spring_boot_readnovel.Dto.Responses.ApiResponse;
import com.vawndev.spring_boot_readnovel.Dto.Responses.PageResponse;
import com.vawndev.spring_boot_readnovel.Dto.Responses.WithdrawResponse;
import com.vawndev.spring_boot_readnovel.Enum.TransactionStatus;
import com.vawndev.spring_boot_readnovel.Services.WithdrawService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/withdraw")
@RequiredArgsConstructor
public class WithdrawController {
    private final WithdrawService withdrawService;

    @PostMapping
    public ApiResponse<WithdrawResponse> withdraw(
            @RequestHeader("Authorization") String bearerToken,
            @RequestBody @Valid WithdrawRequest withdrawRequest) {
        WithdrawResponse response = withdrawService.withdraw(bearerToken, withdrawRequest);
        return ApiResponse.<WithdrawResponse>builder()
                .result(response)
                .message("Successfully withdrawn")
                .build();
    }

    @PutMapping("/{id}")
    public ApiResponse<WithdrawResponse> editWithdraw(
            @RequestHeader("Authorization") String bearerToken,
            @PathVariable("id") String withdrawId,
            @RequestParam TransactionStatus status) {
        WithdrawResponse response = withdrawService.editWithdraw(bearerToken, withdrawId, status);
        return ApiResponse.<WithdrawResponse>builder()
                .result(response)
                .message("Successfully withdrawn")
                .build();
    }



    @GetMapping
    public ApiResponse<PageResponse<WithdrawResponse>> getWithdraws(
            @RequestHeader("Authorization") String bearerToken,
            @RequestParam com.vawndev.spring_boot_readnovel.Enum.TransactionStatus status,
            @RequestBody PageRequest req) {
        PageResponse<WithdrawResponse> response = withdrawService.getWithdraws(bearerToken, status, req);
        return ApiResponse.<PageResponse<WithdrawResponse>>builder()
                .result(response)
                .message("Successfully withdrawn")
                .build();
    }
}
