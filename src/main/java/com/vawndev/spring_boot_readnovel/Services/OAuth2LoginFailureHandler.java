package com.vawndev.spring_boot_readnovel.Services;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Component
@Log4j2
public class OAuth2LoginFailureHandler implements AuthenticationFailureHandler {

    @Value("${url.frontend}")
    private String frontendUrl;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request,
                                        HttpServletResponse response,
                                        AuthenticationException exception) throws IOException {

        String errorCode = "oauth2_login_failed";
        String errorMessage = "Login with Google failed";

        if (exception instanceof OAuth2AuthenticationException oAuth2Ex) {
            OAuth2Error error = oAuth2Ex.getError();
            errorCode = error.getErrorCode();
            errorMessage = error.getDescription();
        }

        // Encode error message để đưa lên URL
        String encodedMessage = URLEncoder.encode(errorMessage, StandardCharsets.UTF_8);

        // Redirect về frontend để xử lý
        String redirectUrl = UriComponentsBuilder
                .fromUriString(frontendUrl + "/oauth2/redirect")
                .queryParam("error", encodedMessage)
                .build().toUriString();

        response.sendRedirect(redirectUrl);
    }
}

