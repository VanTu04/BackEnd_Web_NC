package com.vawndev.spring_boot_readnovel.Controllers;

import com.vawndev.spring_boot_readnovel.Dto.Responses.ApiResponse;
import com.vawndev.spring_boot_readnovel.Dto.Responses.Payment.PaymentResponse;
import com.vawndev.spring_boot_readnovel.Dto.Responses.Payment.WalletTransactionResponse;
import com.vawndev.spring_boot_readnovel.Services.PaymentService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/payment")
public class PaymentController {

    private final PaymentService paymentService;

    @GetMapping("/vn-pay")
    public ApiResponse<PaymentResponse> pay(HttpServletRequest request) {
        return ApiResponse.<PaymentResponse>builder()
                .message("ok")
                .result(paymentService.createVNPayPayment(request))
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

  
}
