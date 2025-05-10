package com.vawndev.spring_boot_readnovel.Controllers;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.vawndev.spring_boot_readnovel.Dto.Responses.ApiResponse;
import com.vawndev.spring_boot_readnovel.Dto.Responses.PageResponse;
import com.vawndev.spring_boot_readnovel.Dto.Responses.PurchaseHistoryDTORes;
import com.vawndev.spring_boot_readnovel.Services.PurchaseHistoryService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/purchase-history")
@RequiredArgsConstructor
public class PurchaseHistoryCotroller {
    private final PurchaseHistoryService psserv;

    @GetMapping
    public ApiResponse<PageResponse<PurchaseHistoryDTORes>> getPurchase(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int limit

    ) {
        return ApiResponse.<PageResponse<PurchaseHistoryDTORes>>builder().result(psserv.getAll(page, limit))
                .build();
    }
}
