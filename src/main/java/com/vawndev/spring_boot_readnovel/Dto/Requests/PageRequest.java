package com.vawndev.spring_boot_readnovel.Dto.Requests;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PageRequest {
    private int page=0;
    private int limit=10;
}
