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
            @RequestBody @Valid WithdrawRequest withdrawRequest) {
        WithdrawResponse response = withdrawService.withdraw( withdrawRequest);
        return ApiResponse.<WithdrawResponse>builder()
                .result(response)
                .message("Successfully withdrawn")
                .build();
    }

    @PutMapping("/{id}")
    public ApiResponse<WithdrawResponse> editWithdraw(
            @PathVariable("id") String withdrawId,
            @RequestParam TransactionStatus status) {
        WithdrawResponse response = withdrawService.editWithdraw( withdrawId, status);
        return ApiResponse.<WithdrawResponse>builder()
                .result(response)
                .message("Successfully withdrawn")
                .build();
    }



    @GetMapping
    public ApiResponse<PageResponse<WithdrawResponse>> getWithdraws(
            @RequestParam com.vawndev.spring_boot_readnovel.Enum.TransactionStatus status,
            @RequestBody PageRequest req) {
        PageResponse<WithdrawResponse> response = withdrawService.getWithdraws( status, req);
        return ApiResponse.<PageResponse<WithdrawResponse>>builder()
                .result(response)
                .message("Successfully withdrawn")
                .build();
    }
}
