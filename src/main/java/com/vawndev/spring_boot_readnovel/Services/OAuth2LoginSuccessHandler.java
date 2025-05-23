package com.vawndev.spring_boot_readnovel.Services;

import com.vawndev.spring_boot_readnovel.Entities.User;
import com.vawndev.spring_boot_readnovel.Exceptions.AppException;
import com.vawndev.spring_boot_readnovel.Exceptions.ErrorCode;
import com.vawndev.spring_boot_readnovel.Repositories.UserRepository;
import com.vawndev.spring_boot_readnovel.Utils.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final UserRepository userRepository;
    private final JwtUtils jwtUtils;

    @Value("${jwt.refreshable-duration}")
    private long refreshDuration;

    @Value("${url.frontend}")
    private String frontend;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        SecurityContextHolder.getContext().setAuthentication(authentication);

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email");

        User existingUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION));

        // Tạo accessToken & refreshToken
        String accessToken = jwtUtils.generateToken(existingUser);
        String refreshToken = jwtUtils.generateRefreshToken(existingUser);

        existingUser.setRefreshToken(refreshToken);
        userRepository.save(existingUser);

        // Tạo cookie chứa refreshToken
        ResponseCookie refreshTokenCookie = jwtUtils.createRefreshTokenCookie(refreshToken, refreshDuration);

        // Gửi cookie vào header
        response.addHeader("Set-Cookie", refreshTokenCookie.toString());

        String redirectUrl = UriComponentsBuilder
                .fromUriString(frontend + "/oauth2/redirect")
                .queryParam("accessToken", accessToken)
                .build().toUriString();

        response.sendRedirect(redirectUrl);
    }
}
