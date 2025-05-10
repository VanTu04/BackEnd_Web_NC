package com.vawndev.spring_boot_readnovel.Services;

import java.util.List;

import com.vawndev.spring_boot_readnovel.Dto.Responses.PageResponse;
import com.vawndev.spring_boot_readnovel.Dto.Responses.PurchaseHistoryDTORes;

public interface PurchaseHistoryService {
    PageResponse<PurchaseHistoryDTORes> getAll(int page, int limit);
}
