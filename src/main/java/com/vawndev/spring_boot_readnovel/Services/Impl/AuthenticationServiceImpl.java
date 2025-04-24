package com.vawndev.spring_boot_readnovel.Services.Impl;

import com.nimbusds.jose.util.Base64;
import com.vawndev.spring_boot_readnovel.Dto.Requests.Auth.AuthenticationRequest;
import com.vawndev.spring_boot_readnovel.Dto.Responses.Auth.AuthenticationResponse;
import com.vawndev.spring_boot_readnovel.Dto.Responses.User.UserResponse;
import com.vawndev.spring_boot_readnovel.Entities.Role;
import com.vawndev.spring_boot_readnovel.Entities.User;
import com.vawndev.spring_boot_readnovel.Exceptions.AppException;
import com.vawndev.spring_boot_readnovel.Exceptions.ErrorCode;
import com.vawndev.spring_boot_readnovel.Mappers.UserMapper;
import com.vawndev.spring_boot_readnovel.Repositories.UserRepository;
import com.vawndev.spring_boot_readnovel.Services.AuthenticationService;
import com.vawndev.spring_boot_readnovel.Utils.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.stream.Collectors;

import static com.vawndev.spring_boot_readnovel.Utils.JwtUtils.*;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final JwtUtils jwtUtils;

    @Override
    public AuthenticationResponse authenticate(AuthenticationRequest request) {

        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        // kiem tra user co trong db khong
        var user = userRepository.findByEmail(request.getEmail()).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        if(StringUtils.hasText(user.getGoogleId())) {
            throw new AppException(ErrorCode.ACCOUNT_FAILE, "Account already exists but created by google account");
        }
        // kiem tra mat khau
            boolean valid = passwordEncoder.matches(request.getPassword(), user.getPassword());
            if(!valid) {
                throw new AppException(ErrorCode.UNAUTHENTICATED);
            }

        // kiem tra tinh trang tai khoan
        if(!user.isActive()) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        // nạp username và password vào security để tạo một authentication object
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword());

        // xác thực người dùng
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        // đưa thông tin xác thực người dùng đã đăng nhập vào Spring Security Context
        SecurityContextHolder.getContext().setAuthentication(authentication);

        var token = jwtUtils.generateToken(user);
        var refreshToken = jwtUtils.generateRefreshToken(user);

        user.setRefreshToken(refreshToken);
        userRepository.save(user);

        return AuthenticationResponse.builder()
                .accessToken(token)
                .refreshToken(refreshToken)
                .role(user.getRoles().stream().map(Role::getName).collect(Collectors.toList()))
                .build();
    }

    @Override
//    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public UserResponse getAccount() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        var user = userRepository.findByEmail(email).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        return userMapper.toUserResponse(user);
    }

    @Override
    public ResponseCookie createRefreshTokenCookie(String refreshToken, long seconds) {
        return ResponseCookie
                .from("refresh_token", refreshToken)
                .httpOnly(true)
                .secure(true)
                .maxAge(seconds)
                .path("/")
                .build();
    }


    @Override
    public AuthenticationResponse generateTokenByRefreshToken(String refreshToken) {
        if(refreshToken == null || refreshToken.isEmpty()) {
            throw new AppException(ErrorCode.MISS_TOKEN);
        }
        User user = jwtUtils.validToken(refreshToken);
        if(!user.getRefreshToken().equals(refreshToken)) {
            throw new AppException(ErrorCode.INVALID_TOKEN);
        }
        String refreshNewToken = jwtUtils.generateRefreshToken(user);
        user.setRefreshToken(refreshNewToken);
        userRepository.save(user);

        return AuthenticationResponse.builder()
                .accessToken(jwtUtils.generateToken(user))
                .refreshToken(refreshNewToken)
                .build();
    }

    @Override
    public ResponseCookie logout(String refreshToken) {
        if(refreshToken == null || refreshToken.isEmpty()) {
            throw new AppException(ErrorCode.MISS_TOKEN);
        }
        User user = jwtUtils.validToken(refreshToken);
        if(!user.getRefreshToken().equals(refreshToken)) {
            throw new AppException(ErrorCode.INVALID_TOKEN);
        }
        user.setRefreshToken(null);
        userRepository.save(user);

        SecurityContextHolder.clearContext();
        return createRefreshTokenCookie("", 0);
    }



}
