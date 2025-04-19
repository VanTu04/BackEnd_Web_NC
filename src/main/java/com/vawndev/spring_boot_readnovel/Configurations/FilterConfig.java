package com.vawndev.spring_boot_readnovel.Configurations;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {

    @Bean
    public FilterRegistrationBean<HostlinkAllowed> hotlinkProtectionFilter() {
        FilterRegistrationBean<HostlinkAllowed> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new HostlinkAllowed());
        registrationBean.addUrlPatterns("/images/*");
        return registrationBean;
    }
}