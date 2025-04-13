package com.vawndev.spring_boot_readnovel.Dto.Requests;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageRequest {
    private int page=0;
    private int limit=10;
}
