package com.vawndev.spring_boot_readnovel.Dto.Requests;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

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
    public static Pageable of(int i, int limit2, Sort descending) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'of'");
    }
}
