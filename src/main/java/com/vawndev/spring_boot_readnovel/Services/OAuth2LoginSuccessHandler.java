package com.vawndev.spring_boot_readnovel.Services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vawndev.spring_boot_readnovel.Dto.Requests.AuthenticationRequest;
import com.vawndev.spring_boot_readnovel.Dto.Responses.ApiResponse;
import com.vawndev.spring_boot_readnovel.Entities.User;
import com.vawndev.spring_boot_readnovel.Exceptions.AppException;
import com.vawndev.spring_boot_readnovel.Exceptions.ErrorCode;
import com.vawndev.spring_boot_readnovel.Repositories.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    @Autowired
    private UserRepository userRepository;


    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Lấy thông tin người dùng từ OAuth2
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email");

        // chắc chắn đã có user
        User existingUser = userRepository.findByEmail(email).orElseThrow(() -> new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION));


        AuthenticationRequest authenticationRequest = AuthenticationRequest.builder()
                .email(email)
                .build();


        //Lưu refreshToken vào cookie
//        CookieUtils.addCookie(response, "refreshToken", refreshToken, 7 * 24 * 60 * 60);

        // Tạo ApiResponse chứa accessToken
        ApiResponse<String> apiResponse = ApiResponse.<String>builder()
                .code(1000)
                .message("Login successful")
                .result("accessToken")
                .build();

        // Trả về JSON
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        ObjectMapper objectMapper = new ObjectMapper();
        response.getWriter().write(objectMapper.writeValueAsString(apiResponse));
    }
}
