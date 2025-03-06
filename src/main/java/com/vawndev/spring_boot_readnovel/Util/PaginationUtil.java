package com.vawndev.spring_boot_readnovel.Util;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

public class PaginationUtil {
    public static Pageable createPageable(Integer page, Integer size) {
        int pageNumber = (page != null && page >= 0) ? page : 0;
        int pageSize = (size != null && size > 0) ? size : 10; // Giá trị mặc định
        return PageRequest.of(pageNumber, pageSize);
    }
}
