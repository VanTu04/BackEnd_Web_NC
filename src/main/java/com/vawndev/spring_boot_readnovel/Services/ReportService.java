package com.vawndev.spring_boot_readnovel.Services;

import java.util.Map;

public interface ReportService {
    Map<String,Object> statistics(Integer month, Integer year);
}
