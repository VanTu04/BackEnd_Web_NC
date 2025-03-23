package com.vawndev.spring_boot_readnovel.Controllers;

import com.vawndev.spring_boot_readnovel.Dto.Responses.ApiResponse;
import com.vawndev.spring_boot_readnovel.Dto.Responses.Payment.PaymentResponse;
import com.vawndev.spring_boot_readnovel.Dto.Responses.Payment.WalletTransactionResponse;
import com.vawndev.spring_boot_readnovel.Services.PaymentService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/payment")
public class PaymentController {

    private final PaymentService paymentService;

    @GetMapping("/vn-pay")
    public ApiResponse<PaymentResponse> pay(HttpServletRequest request, @RequestParam int amount) {
        return ApiResponse.<PaymentResponse>builder()
                .message("ok")
                .result(paymentService.createVNPayPayment(request, amount))
                .build();
    }

    @GetMapping("/vn-pay-callback")
    public ApiResponse<?> payCallbackHandler(@RequestParam Map<String, String> params) {
        String status = params.get("vnp_ResponseCode");
        if (status.equals("00")) {
            return ApiResponse.<WalletTransactionResponse>builder()
                    .message("ok")
                    .result(paymentService.createWalletTransaction(params.get("vnp_TxnRef")))
                    .build();
        } else {
            return ApiResponse.<PaymentResponse>builder()
                    .code(Integer.parseInt(status))
                    .message("failed")
                    .build();
        }
    }

    @PostMapping("/buy-chapter/{chapter_id}")
    public ApiResponse<String> buyChapter(@PathVariable String chapter_id){
        paymentService.purchaseChapter(chapter_id);
        return ApiResponse.<String>builder().message("Success buy chapter!").build();
    }

    @PostMapping("/buy-story/{story_id}")
    public ApiResponse<String> buyStory(@PathVariable String story_id){
        paymentService.purchaseStory(story_id);
        return ApiResponse.<String>builder().message("Success buy chapter!").build();
    }
}
