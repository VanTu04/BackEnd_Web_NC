package com.vawndev.spring_boot_readnovel.Utils;

import lombok.experimental.UtilityClass;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@UtilityClass
public class PaginationUtil {
    public static Pageable createPageable(Integer page, Integer size) {
        int pageNumber = (page != null && page >= 0) ? page : 0;
        int pageSize = (size != null && size > 0) ? size : 10;
        return PageRequest.of(pageNumber, pageSize);
    }

    public static Pageable createPageable(int page, int limit, Sort.Direction direction, String sort) {
        return PageRequest.of(page, limit, Sort.by(direction, sort));
    }

}
