package com.vawndev.spring_boot_readnovel.Services;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Component
@Log4j2
public class OAuth2LoginFailureHandler implements AuthenticationFailureHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void onAuthenticationFailure(HttpServletRequest request,
                                        HttpServletResponse response,
                                        AuthenticationException exception) throws IOException {

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401
        response.setContentType("application/json;charset=UTF-8");

        String errorCode = "oauth2_login_failed";
        String errorMessage = "Login with Google failed";

        // Nếu là OAuth2AuthenticationException → lấy message cụ thể
        if (exception instanceof OAuth2AuthenticationException oAuth2Ex) {
            OAuth2Error error = oAuth2Ex.getError();
            errorCode = error.getErrorCode();
            errorMessage = error.getDescription();
        }

        Map<String, Object> errorResponse = Map.of(
                "code", 401,
                "error", errorCode,
                "message", errorMessage
        );

        String json = objectMapper.writeValueAsString(errorResponse);
        response.getWriter().write(json);
    }
}

