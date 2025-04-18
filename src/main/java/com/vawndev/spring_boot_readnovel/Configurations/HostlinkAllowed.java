package com.vawndev.spring_boot_readnovel.Configurations;

import java.io.IOException;

import org.springframework.stereotype.Component;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class HostlinkAllowed implements Filter {
    private static final String ALLOWED_DOMAIN = "http://localhost:3000";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        String uri = req.getRequestURI();
        if (uri.endsWith(".jpg") || uri.endsWith(".jpeg") || uri.endsWith(".png") || uri.endsWith(".gif")) {
            String referer = req.getHeader("Referer");

            if (referer == null || !referer.startsWith(ALLOWED_DOMAIN)) {
                res.setStatus(HttpServletResponse.SC_FORBIDDEN);
                res.getWriter().write("403 Forbidden - Hotlink not allowed!");
                return;
            }
        }

        chain.doFilter(request, response);
    }
}
